package bean;



public class City {
    private String province;
    private String city;
    private String number;
    private String firstPY;
    private String allPY;
    private String allFirstPY;

    public City(String province,String city,String number,String firstPY,String allPY,String allfirstPY){
        this.province=province;
        this.city=city;
        this.allPY=allPY;
        this.allFirstPY=allfirstPY;
        this.number=number;
        this.firstPY=firstPY;
    }
    public String getCity(){
        return city;
    }

    public String getNumber() {
        return number;
    }

    public String getAllFirstPY() {
        return allFirstPY;
    }

    public String getAllPY() {
        return allPY;
    }

    public String getFirstPY() {
        return firstPY;
    }

    public String getProvince() {
        return province;
    }

    public void setAllFirstPY(String allFirstPY) {
        this.allFirstPY = allFirstPY;
    }

    public void setAllPY(String allPY) {
        this.allPY = allPY;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setFirstPY(String firstPY) {
        this.firstPY = firstPY;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setProvince(String province) {
        this.province = province;
    }

}
