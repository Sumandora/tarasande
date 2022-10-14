package de.florianmichael.vialegacy.api.profile.property;

import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

public class PropertyMap extends ForwardingMultimap<String, Property> {

    private final Multimap<String, Property> properties = LinkedHashMultimap.create();

    @Override
    protected Multimap<String, Property> delegate() {
        return properties;
    }
}
