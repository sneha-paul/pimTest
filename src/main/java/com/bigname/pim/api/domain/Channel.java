package com.bigname.pim.api.domain;

import com.bigname.core.domain.Entity;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotEmpty;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class Channel extends Entity<Channel> {
    @Transient
    @NotEmpty(message = "Channel Id cannot be empty",groups = {CreateGroup.class, DetailsGroup.class})
    String channelId;

    @Indexed(unique = true)
    @NotEmpty(message = "Channel Name cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String channelName;

    public Channel() {
    }

    public String getChannelId() {
        return getExternalId();
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
        setExternalId(channelId);
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    @Override
    protected void setExternalId() {
        this.channelId = getExternalId();
    }

    @Override
    public void orchestrate() {
        super.orchestrate();
        setDiscontinued(getDiscontinued());
        if (booleanValue(getActive()) && booleanValue(getDiscontinued())){
            setActive("N");
        }
    }

    @Override
    public Channel merge(Channel channel) {
        for (String group : channel.getGroup()) {
            switch(group) {
                case "DETAILS" :
    //                this.setExternalId(channel.getExternalId()); - Don't modify the external id after creation
                    this.setChannelName(channel.getChannelName());
                    this.setActive(channel.getActive());
                    this.setDiscontinued(channel.getDiscontinued());
                    break;
            }
        }

        return this;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("channelName", getChannelName());
        map.put("active", getActive());
        map.put("discontinued", getDiscontinued());
        return map;
    }
}
