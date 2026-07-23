package cleanpro.desktopapp.service;

import cleanpro.desktopapp.dao.CleanerDAO;
import cleanpro.desktopapp.dao.MaterialDAO;
import cleanpro.desktopapp.dao.StockIssuanceDAO;
import cleanpro.desktopapp.model.Cleaner;
import cleanpro.desktopapp.model.DashboardSummary;
import cleanpro.desktopapp.model.Material;
import cleanpro.desktopapp.model.RecentIssuance;
import cleanpro.desktopapp.model.StockIssuance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardService {

    private static final int RECENT_ISSUANCES_LIMIT = 10;

    private final MaterialDAO materialDAO = new MaterialDAO();
    private final CleanerDAO cleanerDAO = new CleanerDAO();
    private final StockIssuanceDAO issuanceDAO = new StockIssuanceDAO();

    public DashboardSummary getDashboardSummary() {
        int totalMaterials = materialDAO.getAll().size();

        int lowStockCount = (int) materialDAO.getAll().stream()
                .filter(Material::isLowStock)
                .count();

        int totalActiveCleaners = (int) cleanerDAO.getAll().stream()
                .filter(Cleaner::isActive)
                .count();

        List<RecentIssuance> recentIssuances = getRecentIssuances(RECENT_ISSUANCES_LIMIT);

        return new DashboardSummary(totalMaterials, lowStockCount, totalActiveCleaners, recentIssuances);
    }

    private List<RecentIssuance> getRecentIssuances(int limit) {
        Map<Integer, Cleaner> cleanersById = new HashMap<>();
        for (Cleaner c : cleanerDAO.getAll()) {
            cleanersById.put(c.getCleanerId(), c);
        }

        List<StockIssuance> allIssuances = new ArrayList<>(issuanceDAO.getAll());
        allIssuances.sort(Comparator.comparing(StockIssuance::getIssuanceDate).reversed());

        List<RecentIssuance> results = new ArrayList<>();
        for (StockIssuance issuance : allIssuances) {
            if (results.size() >= limit) {
                break;
            }
            Cleaner cleaner = cleanersById.get(issuance.getCleanerId());
            String cleanerFullName = cleaner != null
                    ? cleaner.getFirstName() + " " + cleaner.getLastName()
                    : "Unknown Cleaner";

            results.add(new RecentIssuance(
                    issuance.getIssuanceNumber(),
                    cleanerFullName,
                    issuance.getIssuanceDate(),
                    issuance.getStatus().name()
            ));
        }
        return results;
    }
}