package com.bigname.pim.api.persistence.dao;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.core.persistence.dao.GenericRepositoryImpl;
import com.bigname.pim.api.domain.VirtualFile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GraphLookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class VirtualFileRepositoryImpl extends GenericRepositoryImpl<VirtualFile> implements VirtualFileRepository {
    public VirtualFileRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, VirtualFile.class);
    }

    //BUG in mongodb returning the proper graph lookup
    public List<VirtualFile> getHierarchyBUG(String rootDirectoryId) {
        List<VirtualFile> assets = new ArrayList<>();
        List<String> assetIds = new ArrayList<>();
        GraphLookupOperation graphLookupOperation = GraphLookupOperation.builder()
                .from("virtualFile")
                .startWith("$_id")
                .connectFrom("parentDirectoryId")
                .connectTo("_id")
                .as("assetsHierarchy");

        Aggregation aggregation = newAggregation(
                match(new Criteria().orOperator(Criteria.where("rootDirectoryId").is(rootDirectoryId), Criteria.where("_id").is(rootDirectoryId))),
                graphLookupOperation
        );

        List<Map<String, Object>> results = mongoTemplate.aggregate(aggregation, "virtualFile", Map.class).getMappedResults().stream().map(CollectionsUtil::generifyMap).collect(Collectors.toList());

        /*results.forEach(assetMap -> {
            List<VirtualFile> nodes = (List<VirtualFile>)assetMap.get("assetsHierarchy");
            for (VirtualFile node : nodes) {
                if (!assetIds.contains(node.getId())) {
                    if (node.getParentDirectoryId().isEmpty()) {
                        assets.add(0, node);
                        assetIds.add(0, node.getId());
                    } else {
                        assets.add(assetIds.indexOf(node.getParentDirectoryId()) + 1, node);
                        assetIds.add(assetIds.indexOf(node.getParentDirectoryId()) + 1, node.getId());
                    }
                }
            }

        });*/

        List<Node> hierarchy = new ArrayList<>();
        results.forEach(assetMap -> {
            List<VirtualFile> assetNodes = (List<VirtualFile>)assetMap.get("assetsHierarchy");
            Node parentNode = null;
            for (VirtualFile assetNode : assetNodes) {
                Node node = new Node(assetNode);
                if(parentNode == null) {
                    if(hierarchy.contains(node)) {
                        parentNode = hierarchy.get(hierarchy.indexOf(node));
                    } else {
                        hierarchy.add(node);
                        parentNode = node;
                    }
                } else {
                    if(parentNode.getChildNodes().contains(node)) {
                        parentNode = parentNode.getChildNodes().get(parentNode.getChildNodes().indexOf(node));
                    } else {
                        parentNode.getChildNodes().add(node);
                        parentNode = node;
                    }
                }
            }
        });
        sort(hierarchy);
        return flatten(hierarchy);
    }

    @Override
    public List<VirtualFile> getHierarchy(String rootDirectoryId, String nodeDirectoryId) {

        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(Criteria.where("rootDirectoryId").is(rootDirectoryId), Criteria.where("_id").is(rootDirectoryId)));
        List<VirtualFile> results = mongoTemplate.find(query, VirtualFile.class);
        List<Node> hierarchy = new ArrayList<>();
        hierarchy.add(nest(results.stream().filter(asset -> asset.getId().equals(rootDirectoryId)).findFirst().orElse(null), results));
        List<VirtualFile> flattenNodes = flatten(Collections.singletonList(sort(extract(nodeDirectoryId, hierarchy))));
        return flattenNodes.isEmpty() ? new ArrayList<>() : flattenNodes.subList(1, flattenNodes.size());
    }

    @Override
    public List<VirtualFile> getFiles(String directoryId) {
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(Criteria.where("parentDirectoryId").is(directoryId)));
        query.with(new Sort(Sort.Direction.ASC, "fileName"));
        return mongoTemplate.find(query, VirtualFile.class);
    }

    private Node extract(String nodeDirectoryId, List<Node> hierarchy) {
        Node extractedNode = null;
        for(Node node : hierarchy) {
            if(node.getNode().getId().equals(nodeDirectoryId)) {
                extractedNode = node;
            } else {
                extractedNode = extract(nodeDirectoryId, node.getChildNodes());
            }
            if(extractedNode != null) {
                break;
            }
        }
        return extractedNode;
    }

    private Node nest(VirtualFile parentAsset, List<VirtualFile> assets) {

        Node node = new Node(parentAsset);

        assets.stream().filter(asset -> asset.getParentDirectoryId().equals(parentAsset.getId()))
                .forEach(asset -> node.getChildNodes().add(nest(asset, assets)));
        return node;
    }



    private static List<VirtualFile> flatten(List<Node> hierarchy) {
        List<VirtualFile> assets = new ArrayList<>();
        for(Node node : hierarchy) {
            assets.add(node.getNode());
            assets.addAll(flatten(node.getChildNodes()));
        }
        return assets;
    }

    private static List<Node> sort(List<Node> hierarchy) {
        if(hierarchy.size() > 1) {
            hierarchy.sort((n1, n2) -> {
                n1 = sort(n1);
                n2 = sort(n2);
                return n1.getNode().getFileName().compareTo(n2.getNode().getFileName());
            });
        } else if(hierarchy.size() == 1) {
            sort(hierarchy.get(0));
        }
        return hierarchy;
    }

    private static Node sort(Node node) {
        if(node.getChildNodes().size() > 1) {
            node.getChildNodes().sort((n1, n2) -> {
                n1 = sort(n1);
                n2 = sort(n2);
                return n1.getNode().getFileName().compareTo(n2.getNode().getFileName());
            });
        } else if(node.getChildNodes().size() == 1) {
            sort(node.getChildNodes().get(0));
        }
        return node;
    }

    class Node {
        private VirtualFile node;
        private List<Node> childNodes = new ArrayList<>();

        public Node(VirtualFile node) {
            this.node = node;
        }

        public VirtualFile getNode() {
            return node;
        }

        public List<Node> getChildNodes() {
            return childNodes;
        }

        public Node addNode(Node node) {
            childNodes.add(node);
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node1 = (Node) o;

            return node != null ? node.getId().equals(node1.node.getId()) : node1.node.getId() == null;
        }


    }
}
