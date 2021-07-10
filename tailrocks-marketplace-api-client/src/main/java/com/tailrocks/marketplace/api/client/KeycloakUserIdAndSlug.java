package com.tailrocks.marketplace.api.client;

import io.micronaut.core.annotation.NonNull;

/**
 * @author Alexey Zhokhov
 */
public record KeycloakUserIdAndSlug(@NonNull String keycloakUserId, @NonNull String slug) {

}
