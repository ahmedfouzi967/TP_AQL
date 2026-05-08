package exercice3;

public interface ProductApiClient {
    /**
     * Récupère les détails d'un produit depuis l'API externe.
     *
     * @param productId identifiant du produit
     * @return le produit trouvé, ou null si le format est incompatible
     * @throws ApiException en cas d'échec de l'appel API
     */
    Product getProduct(String productId);
}
