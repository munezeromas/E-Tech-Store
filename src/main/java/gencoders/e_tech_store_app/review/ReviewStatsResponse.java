// ReviewStatsResponse.java
package gencoders.e_tech_store_app.review;

import java.util.Map;

public record ReviewStatsResponse(
        double average,
        long total,
        Map<Integer, Long> distribution   // 1→#, 2→#, … 5→#
) { }
