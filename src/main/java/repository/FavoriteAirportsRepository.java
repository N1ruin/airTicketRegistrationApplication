package repository;


public interface FavoriteAirportsRepository {
    void addFavorite(Long passengerId, String airportCode);

    void removeFavorite(Long passengerId, String airportCode);
}
