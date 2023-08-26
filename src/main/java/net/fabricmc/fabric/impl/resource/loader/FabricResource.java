package net.fabricmc.fabric.impl.resource.loader;

import net.minecraft.resource.ResourcePackSource;
import org.slf4j.LoggerFactory;

public interface FabricResource {
    /**
     * Gets the resource pack source of this resource.
     * The source is used to separate vanilla/mod resources from user resources in Fabric API.
     *
     * <p>Custom {@link net.minecraft.resource.Resource} implementations should override this method.
     *
     * @return the resource pack source
     */
    default ResourcePackSource getFabricPackSource() {
        LoggerFactory.getLogger(FabricResource.class).error("Unknown Resource implementation {}, returning PACK_SOURCE_NONE as the source", getClass().getName());
        return PACK_SOURCE_NONE;
    }

    ResourcePackSource PACK_SOURCE_NONE = ResourcePackSource.method_29486("pack.source.none");
}