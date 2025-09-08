package org.os.bayturabackend.DTOs;

import lombok.Data;
import org.os.bayturabackend.entities.Media;
import org.os.bayturabackend.entities.PropertyStatus;
import org.os.bayturabackend.entities.PropertyType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PropertyResponse {

    private Long id;
    private String title;
    private String description;
    private PropertyType type;
    private PropertyStatus propertyStatus;
    private double price;
    private Double area;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long ownerId;

    private BigDecimal latitude;
    private BigDecimal longitude;
    private List<Media> images;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public PropertyType getType() {
        return type;
    }
    public void setType(PropertyType type) {
        this.type = type;
    }
    public PropertyStatus getPropertyStatus() {
        return propertyStatus;
    }
    public void setPropertyStatus(PropertyStatus propertyStatus) {
        this.propertyStatus = propertyStatus;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public Double getArea() {
        return area;
    }
    public void setArea(Double area) {
        this.area = area;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Long getOwner() {
        return ownerId;
    }
    public void setOwner(Long owner) {
        this.ownerId = ownerId;
    }
    public BigDecimal getLatitude() {
        return latitude;
    }
    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
    public BigDecimal getLongitude() {
        return longitude;
    }
    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
    public List<Media> getImages() {
        return images;
    }
    public void setImages(List<Media> images) {
        this.images = images;
    }

}
