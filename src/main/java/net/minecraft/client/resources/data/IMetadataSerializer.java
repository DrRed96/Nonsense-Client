package net.minecraft.client.resources.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IRegistry;
import net.minecraft.util.RegistrySimple;

public class IMetadataSerializer
{
    private final IRegistry < String, IMetadataSerializer.Registration <? extends IMetadataSection >> metadataSectionSerializerRegistry = new RegistrySimple<>();
    private final GsonBuilder gsonBuilder = new GsonBuilder();

    /**
     * Cached Gson instance. Set to null when more sections are registered, and then re-created from the builder.
     */
    private Gson gson;

    public IMetadataSerializer()
    {
        this.gsonBuilder.registerTypeHierarchyAdapter(IChatComponent.class, new IChatComponent.Serializer());
        this.gsonBuilder.registerTypeHierarchyAdapter(ChatStyle.class, new ChatStyle.Serializer());
        this.gsonBuilder.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
    }

    public <T extends IMetadataSection> void registerMetadataSectionType(IMetadataSectionSerializer<T> p_110504_1_, Class<T> p_110504_2_)
    {
        this.metadataSectionSerializerRegistry.putObject(p_110504_1_.getSectionName(), new IMetadataSerializer.Registration<>(p_110504_1_, p_110504_2_));
        this.gsonBuilder.registerTypeAdapter(p_110504_2_, p_110504_1_);
        this.gson = null;
    }

    public <T extends IMetadataSection> T parseMetadataSection(String section, JsonObject jsonObject)
    {
        if (section == null)
        {
            throw new IllegalArgumentException("Metadata section name cannot be null");
        }
        else if (!jsonObject.has(section))
        {
            return null;
        }
        else if (!jsonObject.get(section).isJsonObject())
        {
            throw new IllegalArgumentException("Invalid metadata for '" + section + "' - expected object, found " + jsonObject.get(section));
        }
        else
        {
            IMetadataSerializer.Registration<?> registration = this.metadataSectionSerializerRegistry.getObject(section);

            if (registration == null)
            {
                throw new IllegalArgumentException("Don't know how to handle metadata section '" + section + "'");
            }
            else
            {
                return (T) this.getGson().fromJson(jsonObject.getAsJsonObject(section), registration.type);
            }
        }
    }

    /**
     * Returns a Gson instance with type adapters registered for metadata sections.
     */
    private Gson getGson()
    {
        if (this.gson == null)
        {
            this.gson = this.gsonBuilder.create();
        }

        return this.gson;
    }

    class Registration<T extends IMetadataSection>
    {
        final IMetadataSectionSerializer<T> serializer;
        final Class<T> type;

        private Registration(IMetadataSectionSerializer<T> serializer, Class<T> type)
        {
            this.serializer = serializer;
            this.type = type;
        }
    }
}
