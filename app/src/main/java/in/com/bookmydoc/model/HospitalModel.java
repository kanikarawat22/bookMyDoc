package in.com.bookmydoc.model;

import java.util.List;

public class HospitalModel {
    private String id;
    private String name;
    private String address;     // ✅ added address field
    private String logoUrl;     // ✅ added logoUrl field
    private double rating;
    private int ratingCount;
    private int distance;
    private List<String> imageUrls;

    public HospitalModel() {}

    public HospitalModel(String id, String name, String address, String logoUrl,
                         double rating, int ratingCount, int distance, List<String> imageUrls) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.logoUrl = logoUrl;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.distance = distance;
        this.imageUrls = imageUrls;
    }

    // 🔹 Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }

    public int getDistance() { return distance; }
    public void setDistance(int distance) { this.distance = distance; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
}
