package Bean;


public class AppVersion  {

    private Integer type;
    private String version;
    private String url;
    private Integer forceUpdate;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(Integer forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    @Override
    public String toString() {
        return "AppVersion{" +
                "type=" + type +
                ", version='" + version + '\'' +
                ", url='" + url + '\'' +
                ", forceUpdate=" + forceUpdate +
                "} " ;
    }
}