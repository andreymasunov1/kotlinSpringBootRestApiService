package org.dci.woltjuniorsoftwareengineer.enums

/**
 * Enum class representing venue slugs for different locations.
 * Each enum value includes a specific slug string to uniquely identify the venue.
 *
 * @property slug the unique identifier for the venue, used in API requests and responses.
 */
enum class VenueSlug(val slug: String) {

    /** Venue slug for Helsinki location. */
    HELSINKI("home-assignment-venue-helsinki"),

    /** Venue slug for Stockholm location. */
    STOCKHOLM("home-assignment-venue-stockholm"),

    /** Venue slug for Berlin location. */
    BERLIN("home-assignment-venue-berlin")
}
