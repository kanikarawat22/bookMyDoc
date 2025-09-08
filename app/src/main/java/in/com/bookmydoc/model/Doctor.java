package in.com.bookmydoc.model;

public class Doctor {
    private String docid;
    private String name;
    private String specialty;
    private String hospital;
    private String experience;
    private String profileImageUrl;
    private float rating;
    private double fee = 0.0;

    public Doctor() {} // Required for Firebase

    public Doctor(String docid, String name, String specialty, String hospital, String experience, String profileImageUrl, float rating, double fee) {
        this.docid = docid;
        this.name = name;
        this.specialty = specialty;
        this.hospital = hospital;
        this.experience = experience;
        this.profileImageUrl = profileImageUrl;
        this.rating = rating;
        this.fee= fee;
    }

    // Getters & Setters (only the necessary ones shown for brevity)
    public String getName() { return name; }
    public String getDocid() { return docid; }
    public String getSpecialty() { return specialty; }
    public String getHospital() { return hospital; }
    public String getExperience() { return experience; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public float getRating() { return rating; }
    public double getFee() { return fee; }

    public void setDocid(String docid) { this.docid = docid; }
    public void setName(String name) { this.name = name; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public void setHospital(String hospital) { this.hospital = hospital; }
    public void setExperience(String experience) { this.experience = experience; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public void setRating(float rating) { this.rating = rating; }
    public void setFee(double fee) { this.fee = fee; }
}

