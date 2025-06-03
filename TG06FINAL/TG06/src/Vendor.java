import java.time.*;

public class Vendor {
    private int vendorId;
    private LocalDateTime registerDate;
    private String vendorName;
    private String ownerName;
    private String ownerPhone;
    private String vendorPhone;
    private String account;
    private String password;
    private String address;
    private String email;
    private String type;
    private String description;
    private String mapUrl;
    private String status;
    private boolean monOpen;
    private boolean tueOpen;
    private boolean wedOpen;
    private boolean thuOpen;
    private boolean friOpen;
    private boolean satOpen;
    private boolean sunOpen;
    private LocalTime monStartTime;
    private LocalTime monEndTime;
    private LocalTime tueStartTime;
    private LocalTime tueEndTime;
    private LocalTime wedStartTime;
    private LocalTime wedEndTime;
    private LocalTime thuStartTime;
    private LocalTime thuEndTime;
    private LocalTime friStartTime;
    private LocalTime friEndTime;
    private LocalTime satStartTime;
    private LocalTime satEndTime;
    private LocalTime sunStartTime;
    private LocalTime sunEndTime;

    public Vendor() {}

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public LocalDateTime getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDateTime registerDate) {
        this.registerDate = registerDate;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getVendorPhone() {
        return vendorPhone;
    }

    public void setVendorPhone(String vendorPhone) {
        this.vendorPhone = vendorPhone;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isMonOpen() {
        return monOpen;
    }

    public void setMonOpen(boolean monOpen) {
        this.monOpen = monOpen;
    }

    public boolean isTueOpen() {
        return tueOpen;
    }

    public void setTueOpen(boolean tueOpen) {
        this.tueOpen = tueOpen;
    }

    public boolean isWedOpen() {
        return wedOpen;
    }

    public void setWedOpen(boolean wedOpen) {
        this.wedOpen = wedOpen;
    }

    public boolean isThuOpen() {
        return thuOpen;
    }

    public void setThuOpen(boolean thuOpen) {
        this.thuOpen = thuOpen;
    }

    public boolean isFriOpen() {
        return friOpen;
    }

    public void setFriOpen(boolean friOpen) {
        this.friOpen = friOpen;
    }

    public boolean isSatOpen() {
        return satOpen;
    }

    public void setSatOpen(boolean satOpen) {
        this.satOpen = satOpen;
    }

    public boolean isSunOpen() {
        return sunOpen;
    }

    public void setSunOpen(boolean sunOpen) {
        this.sunOpen = sunOpen;
    }

    public LocalTime getMonStartTime() {
        return monStartTime;
    }

    public void setMonStartTime(LocalTime monStartTime) {
        this.monStartTime = monStartTime;
    }

    public LocalTime getMonEndTime() {
        return monEndTime;
    }

    public void setMonEndTime(LocalTime monEndTime) {
        this.monEndTime = monEndTime;
    }

    public LocalTime getTueStartTime() {
        return tueStartTime;
    }

    public void setTueStartTime(LocalTime tueStartTime) {
        this.tueStartTime = tueStartTime;
    }

    public LocalTime getTueEndTime() {
        return tueEndTime;
    }

    public void setTueEndTime(LocalTime tueEndTime) {
        this.tueEndTime = tueEndTime;
    }

    public LocalTime getWedStartTime() {
        return wedStartTime;
    }

    public void setWedStartTime(LocalTime wedStartTime) {
        this.wedStartTime = wedStartTime;
    }

    public LocalTime getWedEndTime() {
        return wedEndTime;
    }

    public void setWedEndTime(LocalTime wedEndTime) {
        this.wedEndTime = wedEndTime;
    }

    public LocalTime getThuStartTime() {
        return thuStartTime;
    }

    public void setThuStartTime(LocalTime thuStartTime) {
        this.thuStartTime = thuStartTime;
    }

    public LocalTime getThuEndTime() {
        return thuEndTime;
    }

    public void setThuEndTime(LocalTime thuEndTime) {
        this.thuEndTime = thuEndTime;
    }

    public LocalTime getFriStartTime() {
        return friStartTime;
    }

    public void setFriStartTime(LocalTime friStartTime) {
        this.friStartTime = friStartTime;
    }

    public LocalTime getFriEndTime() {
        return friEndTime;
    }

    public void setFriEndTime(LocalTime friEndTime) {
        this.friEndTime = friEndTime;
    }

    public LocalTime getSatStartTime() {
        return satStartTime;
    }

    public void setSatStartTime(LocalTime satStartTime) {
        this.satStartTime = satStartTime;
    }

    public LocalTime getSatEndTime() {
        return satEndTime;
    }

    public void setSatEndTime(LocalTime satEndTime) {
        this.satEndTime = satEndTime;
    }

    public LocalTime getSunStartTime() {
        return sunStartTime;
    }

    public void setSunStartTime(LocalTime sunStartTime) {
        this.sunStartTime = sunStartTime;
    }

    public LocalTime getSunEndTime() {
        return sunEndTime;
    }

    public void setSunEndTime(LocalTime sunEndTime) {
        this.sunEndTime = sunEndTime;
    }
}
