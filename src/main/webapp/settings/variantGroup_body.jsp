<%--@elvariable id="variantGroup" type="com.bigname.pim.core.domain.VariantGroup"--%>
<%--@elvariable id="variantGroupAttributes" type="java.util.Map<String, java.util.List<com.bigname.pim.api.domain.FamilyAttribute>"--%>
<%--@elvariable id="variantGroupAxisAttributes" type="java.util.Map<String, java.util.List<com.bigname.pim.api.domain.FamilyAttribute>"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="header">
                <h2>${variantGroup.name}
                    <small><code class="highlighter-rouge">${variantGroup.id}</code></small>
                    <%--<small class="pull-right m-t--15"><code style="color:#808080">_id: ${variantGroup.id}</code>--%>
                    <%--</small>--%>
                </h2>
            </div>
            <div class="body">
                <ul class="nav nav-tabs-new2">
                    <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#details" onclick="window.location.hash=''">Details</a></li>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#axisAttributes">Axis Attributes</a></li>
                    <c:if test="${not empty variantGroupAxisAttributes['AXIS_ATTRIBUTES_L1'] and (variantGroup.level eq 1 or not empty variantGroupAxisAttributes['AXIS_ATTRIBUTES_L2'])}">
                        <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#variantAttributes">Child Product Attributes</a></li>
                    </c:if>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane show active" id="details">
                        <div class="row clearfix m-t-20">
                            <div class="col-lg-12 col-md-12">
                                <div class="card">
                                    <div class="body">
                                        <form method="post"
                                              action="/pim/families/${variantGroup.familyId}/variantGroups/${variantGroup.id}"
                                              data-method="PUT"
                                              data-success-message='["Successfully updated the variant group", "Variant Group Updated"]'
                                              data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                                            <div class="row">
                                                <div class="col-md-6 col-sm-12">
                                                    <div class="form-group">
                                                        <label for="name">Group Name</label><code
                                                            class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="name" name="name"
                                                               value="${variantGroup.name}"
                                                               class="form-control"/>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="id">Group ID</label><code
                                                            class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="id" name="id"
                                                               class="form-control"
                                                               value="${variantGroup.id}"/>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="levels">Levels</label><code
                                                            class="highlighter-rouge m-l-10">*</code>
                                                        <input type="text" id="levels" name="levels"
                                                               class="form-control" disabled="disabled"
                                                               value="${variantGroup.level}"/>
                                                    </div>
                                                    <div class="form-group">
                                                        <label>Status</label>
                                                        <br/>
                                                        <label for="active" class="fancy-checkbox">
                                                            <input type="checkbox" id="active" name="active" value="Y"
                                                                   <c:if test="${variantGroup.active eq 'Y'}">checked="checked"</c:if>>
                                                            <span>Active</span>
                                                        </label>
                                                    </div>
                                                </div>
                                            </div>
                                            <br>
                                            <input type="hidden" name="group" value="DETAILS"/>
                                            <button type="submit" class="btn btn-primary"
                                                    onclick="$.submitAction(event, this)">Save
                                            </button>
                                            <a href="/pim/families/${variantGroup.familyId}#variantGroups">
                                                <button type="button" class="btn btn-danger">Cancel</button>
                                            </a>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="axisAttributes">
                        <div class="taskboard">
                            <div class="row clearfix">
                                <div class="col-lg-4 col-md-12">
                                    <div class="card bg-dark planned_task">
                                        <div class="header">
                                            <h2>Parent Product Attributes</h2>
                                            <%--<ul class="header-dropdown">
                                                <li><a href="javascript:void(0);" data-toggle="modal" data-target="#addcontact"><i class="icon-plus"></i></a></li>
                                            </ul>--%>
                                        </div>
                                        <div class="body">
                                            <div id="availableAxisAttributes" class="dd" data-plugin="nestable">
                                                <c:forEach var="attribute" items="${variantGroupAxisAttributes['AVAILABLE_AXIS_ATTRIBUTES']}" varStatus="s1">
                                                    <c:if test="${s1.first}"><ol class="dd-list"></c:if>
                                                        <li class="dd-item" data-id="${attribute.id}">
                                                            <div class="dd-handle">
                                                                <h6>${attribute.name}</h6>
                                                            </div>
                                                        </li>
                                                    <c:if test="${s1.last}"></ol></c:if>
                                                </c:forEach>
                                                <c:if test="${empty variantGroupAxisAttributes['AVAILABLE_AXIS_ATTRIBUTES']}">
                                                    <div class="dd-empty"></div>
                                                </c:if>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-lg-4 col-md-12">
                                    <div class="card bg-dark progress_task">
                                        <div class="header">
                                            <h2>Axis Attributes - Level 1</h2>
                                            <%--<ul class="header-dropdown">
                                                <li><a href="javascript:void(0);" data-toggle="modal" data-target="#addcontact"><i class="icon-plus"></i></a></li>
                                            </ul>--%>
                                        </div>
                                        <div class="body">
                                            <div id="axisAttributesL1" class="dd" data-plugin="nestable">
                                                <c:forEach var="attribute" items="${variantGroupAxisAttributes['AXIS_ATTRIBUTES_L1']}" varStatus="s1">
                                                    <c:if test="${s1.first}"><ol class="dd-list"></c:if>
                                                    <li class="dd-item" data-id="${attribute.id}">
                                                        <div class="dd-handle">
                                                            <h6>${attribute.name}</h6><small class="pull-right m-t--25"><code class="highlighter-rouge">VARIANT AXIS</code></small>
                                                        </div>
                                                    </li>
                                                    <c:if test="${s1.last}"></ol></c:if>
                                                </c:forEach>
                                                <c:if test="${empty variantGroupAxisAttributes['AXIS_ATTRIBUTES_L1']}">
                                                    <div class="dd-empty"></div>
                                                </c:if>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <c:if test="${variantGroup.level eq 2}">
                                    <div class="col-lg-4 col-md-12">
                                        <div class="card bg-dark progress_task">
                                            <div class="header">
                                                <h2>Axis Attributes - Level 2</h2>
                                                    <%--<ul class="header-dropdown">
                                                        <li><a href="javascript:void(0);" data-toggle="modal" data-target="#addcontact"><i class="icon-plus"></i></a></li>
                                                    </ul>--%>
                                            </div>
                                            <div class="body">
                                                <div id="axisAttributesL2" class="dd" data-plugin="nestable">
                                                    <c:forEach var="attribute" items="${variantGroupAxisAttributes['AXIS_ATTRIBUTES_L2']}" varStatus="s1">
                                                        <c:if test="${s1.first}"><ol class="dd-list"></c:if>
                                                        <li class="dd-item" data-id="${attribute.id}">
                                                            <div class="dd-handle">
                                                                <h6>${attribute.name}</h6><small class="pull-right m-t--25"><code class="highlighter-rouge">VARIANT AXIS</code></small>
                                                            </div>
                                                        </li>
                                                        <c:if test="${s1.last}"></ol></c:if>
                                                    </c:forEach>
                                                    <c:if test="${empty variantGroupAxisAttributes['AXIS_ATTRIBUTES_L2']}">
                                                        <div class="dd-empty"></div>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="variantAttributes">
                        <div class="taskboard">
                            <div class="row clearfix">
                                <div class="col-lg-4 col-md-12">
                                    <div class="card bg-dark planned_task">
                                        <div class="header">
                                            <h2>Parent Product Attributes</h2>
                                            <%--<ul class="header-dropdown">
                                                <li><a href="javascript:void(0);" data-toggle="modal" data-target="#addcontact"><i class="icon-plus"></i></a></li>
                                            </ul>--%>
                                        </div>
                                        <div class="body">
                                            <div id="product" class="dd" data-plugin="nestable">
                                                <ol class="dd-list">
                                                    <c:forEach var="attribute" items="${variantGroupAttributes['PRODUCT_ATTRIBUTES']}">
                                                    <li class="dd-item" data-id="${attribute.id}">
                                                        <div class="dd-handle">
                                                            <h6>${attribute.name}</h6>
                                                        </div>
                                                    </li>
                                                    </c:forEach>
                                                </ol>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-lg-4 col-md-12">
                                    <div class="card bg-dark progress_task">
                                        <div class="header">
                                            <h2>Child Product Attributes - Level 1</h2>
                                            <%--<ul class="header-dropdown">
                                                <li><a href="javascript:void(0);" data-toggle="modal" data-target="#addcontact"><i class="icon-plus"></i></a></li>
                                            </ul>--%>
                                        </div>
                                        <div class="body">
                                            <div id="variantL1" class="dd" data-plugin="nestable">
                                                <ol class="dd-list">
                                                    <c:forEach var="attribute" items="${variantGroupAttributes['AXIS_ATTRIBUTES_L1']}">
                                                        <li class="dd-item dd-nodrag" data-id="${attribute.id}">
                                                            <div class="dd-handle">
                                                                <h6>${attribute.name}</h6><small class="pull-right m-t--25"><code class="highlighter-rouge">VARIANT AXIS</code></small>
                                                            </div>
                                                        </li>
                                                    </c:forEach>

                                                    <c:forEach var="attribute" items="${variantGroupAttributes['VARIANT_ATTRIBUTES_L1']}">
                                                        <li class="dd-item" data-id="${attribute.id}">
                                                            <div class="dd-handle">
                                                                <h6>${attribute.name}</h6>
                                                            </div>
                                                        </li>
                                                    </c:forEach>
                                                </ol>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <c:if test="${variantGroup.level eq 2}">
                                    <div class="col-lg-4 col-md-12">
                                        <div class="card bg-dark progress_task">
                                            <div class="header">
                                                <h2>Variant Attributes - Level 2</h2>
                                                    <%--<ul class="header-dropdown">
                                                        <li><a href="javascript:void(0);" data-toggle="modal" data-target="#addcontact"><i class="icon-plus"></i></a></li>
                                                    </ul>--%>
                                            </div>
                                            <div class="body">
                                                <div id="variantL2" class="dd" data-plugin="nestable">
                                                    <ol class="dd-list">
                                                        <c:forEach var="attribute" items="${variantGroupAttributes['AXIS_ATTRIBUTES_L2']}">
                                                            <li class="dd-item dd-nodrag" data-id="${attribute.id}">
                                                                <div class="dd-handle">
                                                                    <h6>${attribute.name}</h6><small class="pull-right m-t--25"><code class="highlighter-rouge">VARIANT AXIS</code></small>
                                                                </div>
                                                            </li>
                                                        </c:forEach>

                                                        <c:forEach var="attribute" items="${variantGroupAttributes['VARIANT_ATTRIBUTES_L2']}">
                                                            <li class="dd-item" data-id="${attribute.id}">
                                                                <div class="dd-handle">
                                                                    <h6>${attribute.name}</h6>
                                                                </div>
                                                            </li>
                                                        </c:forEach>
                                                    </ol>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    $.initPage({
        'familyId': '${variantGroup.familyId}',
        'variantGroupId': '${variantGroup.id}'
    });
</script>
<script src="/assets/js/pages/ui/settings/variantGroup.js"></script>

