package um.ico.ingenierie.Api.DTO;

// C'est pour avoir des donnée mais la logique est pas encore implémenter.

public record GraphD3DTO(
        String encoreRien
){
    public static GraphD3DTO from(String attend){
        return new GraphD3DTO(attend);
    }
}
