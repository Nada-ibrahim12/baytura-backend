package org.os.bayturabackend.specifications;

import org.os.bayturabackend.entities.Property;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class PropertySpecification {

    public static Specification<Property> buildSpec(
            String type,
            String purpose,
            String query,
            Double minPrice, Double maxPrice,
            Double minArea, Double maxArea,
            String username
    ) {
        return (root, q, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (type != null) {
                predicates.add(cb.equal(cb.lower(root.get("type")), type.toLowerCase()));
            }

            if (purpose != null) {
                predicates.add(cb.equal(cb.lower(root.get("purpose")), purpose.toLowerCase()));
            }

            if (query != null && !query.isBlank()) {
                String likeQuery = "%" + query.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), likeQuery),
                        cb.like(cb.lower(root.get("description")), likeQuery),
                        cb.like(cb.lower(root.get("address")), likeQuery)
                ));
            }

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            if (minArea != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("area"), minArea));
            }
            if (maxArea != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("area"), maxArea));
            }

            if (username != null) {
                String likeUser = "%" + username.toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("owner").get("username")), likeUser));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
