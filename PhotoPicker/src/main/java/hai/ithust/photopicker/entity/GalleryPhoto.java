package hai.ithust.photopicker.entity;

/**
 * @author conghai on 12/20/18.
 */
public class GalleryPhoto {

    private int id;
    private String path;

    public GalleryPhoto(int id, String path) {
        this.id = id;
        this.path = path;
    }

    public GalleryPhoto() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GalleryPhoto)) return false;

        GalleryPhoto photo = (GalleryPhoto) o;

        return id == photo.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
