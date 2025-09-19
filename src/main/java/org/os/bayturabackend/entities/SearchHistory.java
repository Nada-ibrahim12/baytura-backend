package org.os.bayturabackend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "search_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_history_id")
    private Long id;

    @Column(name = "search_query",nullable = false)
    private String searchQuery;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "filters_used", columnDefinition = "json")
    private Map<String, Object> filtersUsed;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Customer customer;

}
