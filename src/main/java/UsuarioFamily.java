public class UsuarioFamily {
    private String id;
    private String nome;
    private boolean ehAdulto;

    public UsuarioFamily(String id, String nome, boolean ehAdulto) {
        this.id = id;
        this.nome = nome;
        this.ehAdulto = ehAdulto;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public boolean isEhAdulto() {
        return ehAdulto;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEhAdulto(boolean ehAdulto) {
        this.ehAdulto = ehAdulto;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UsuarioFamily that = (UsuarioFamily) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
