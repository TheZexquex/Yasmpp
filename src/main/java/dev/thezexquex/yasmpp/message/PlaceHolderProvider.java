package dev.thezexquex.yasmpp.message;

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public interface PlaceHolderProvider {

    TagResolver[] tagResolvers();
}
