package MaidRepository.maid.dto;

import lombok.Data;
import java.util.List;

@Data
public class ORSRouteResponse {
    private List<Route> routes;
    private Metadata metadata;

    @Data
    public static class Route {
        private Summary summary;
        private List<List<Double>> geometry;
        private List<Segment> segments;
    }

    @Data
    public static class Summary {
        private Double distance; // in meters
        private Double duration; // in seconds
    }

    @Data
    public static class Segment {
        private Double distance;
        private Double duration;
        private List<Step> steps;
    }

    @Data
    public static class Step {
        private Double distance;
        private Double duration;
        private String instruction;
        private String name;
    }

    @Data
    public static class Metadata {
        private String attribution;
        private String query;
    }
}