package org.deletethis.search.parser.patched;

import org.deletethis.search.parser.*;
import org.deletethis.search.parser.internal.util.ByteArrays;
import org.deletethis.search.parser.internal.xml.PoorXmlWriter;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class PatchedPlugin implements SearchPlugin {
    private final SearchPlugin searchPlugin;
    private String checksum;
    private final Map<String, String> attr;

    private final String name;
    private final SearchPluginIcon icon;

    private final static String NS_PREFIX = "n";
    private final static Map<String, String> NSMAP = new HashMap<>();
    static {
        NSMAP.put(NS_PREFIX, PatchedConstants.NAMESPACE);
    }

    public PatchedPlugin(PatchBuilder patchBuilder) {
        this.searchPlugin = Objects.requireNonNull(patchBuilder.getPlugin());
        this.name = patchBuilder.getName();
        this.attr = Collections.unmodifiableMap(patchBuilder.getAttributes());
        this.icon = patchBuilder.getIcon();
    }

    @Override
    public String getName() {
        if(name != null) {
            return name;
        } else {
            return searchPlugin.getName();
        }
    }

    @Override
    public String getSearchUrl(SearchQuery search) {
        return searchPlugin.getSearchUrl(search);
    }

    @Override
    public Optional<String> getUpdateUrl() {
        return searchPlugin.getUpdateUrl();
    }

    @Override
    public SearchPluginIcon getIcon() {
        if(icon != null) {
            return icon;
        } else {
            return searchPlugin.getIcon();
        }
    }

    @Override
    public boolean supportsSuggestions() {
        return searchPlugin.supportsSuggestions();
    }

    @Override
    public SuggestionRequest getSuggestions(SearchQuery search) {
        return searchPlugin.getSuggestions(search);
    }

    @Override
    public Map<PropertyName, PropertyValue> getProperties() {
        return searchPlugin.getProperties();
    }

    @Override
    public String getIdentifier() {
        return searchPlugin.getIdentifier();
    }

    @Override
    public byte[] serialize() {
        PoorXmlWriter writer = new PoorXmlWriter();
        writer.startDocument();
        writer.startElement(NS_PREFIX, PatchedConstants.ROOT_ELEMENT, NSMAP);
        if (name != null) {
            writer.textElement(NS_PREFIX, PatchedConstants.NAME_ELEMENT, name);
        }
        for (Map.Entry<String, String> e : attr.entrySet()) {
            writer.startElement(NS_PREFIX, PatchedConstants.ATTR_ELEMENT);
            writer.textElement(NS_PREFIX, PatchedConstants.ATTR_NAME_ELEMENT, e.getKey());
            writer.textElement(NS_PREFIX, PatchedConstants.ATTR_VALUE_ELEMENT, e.getValue());
            writer.endElement();
        }
        if(icon != null) {
            writer.startElement(NS_PREFIX, PatchedConstants.ICON_ELEMENT);
            for(String s: icon) {
                writer.textElement(NS_PREFIX, PatchedConstants.ICON_IMAGE_ELEMENT, s);
            }
            writer.endElement();
        }
        String serialized = ByteArrays.encodeBase64(searchPlugin.serialize());
        writer.textElement(NS_PREFIX, PatchedConstants.SOURCE_ELEMENT, serialized);
        writer.endElement();
        writer.endDocument();
        return writer.toByteArray();
    }

    @Override
    public PatchBuilder patch() {
        PatchBuilder patch = searchPlugin.patch();
        return patch.name(name);
    }

    @Override
    public String getChecksum() {
        // synchronization not needed, reference writes are atomic
        if(checksum == null) {
            StringBuilder bld = new StringBuilder(1024);
            bld.append(searchPlugin.getChecksum());
            if(name != null) {
                bld.append("-NAME-").append(name);
            }
            for (Map.Entry<String, String> e : attr.entrySet()) {
                bld.append("-A-NAME-").append(e.getKey());
                bld.append("-A-VALUE-").append(e.getValue());
            }
            if(icon != null) {
                bld.append("-ICON-").append(icon.hashCode());
            }
            byte[] bytes = bld.toString().getBytes(StandardCharsets.UTF_8);
            this.checksum = ByteArrays.sha1Sum(bytes);
        }
        return checksum;
    }

    @Override
    public Map<String, String> getAttributes() {
        return attr;
    }
}
