package cleanpro.desktopapp.model;

import java.util.List;

/**
 * Simple data holder for the four headline numbers shown on the Dashboard,
 * plus the list of most recent issuances shown in the "recent activity" table.
 *
 * This is a plain DTO (Data Transfer Object) - it has no logic, it just carries
 * data from the BLL layer to the UI layer.
 */
public class DashboardSummary {

    private final int totalMaterials;
    private final int lowStockCount;
    private final int totalCleaners;
    private final List<RecentIssuance> recentIssuances;

    public DashboardSummary(int totalMaterials, int lowStockCount, int totalCleaners,
                             List<RecentIssuance> recentIssuances) {
        this.totalMaterials = totalMaterials;
        this.lowStockCount = lowStockCount;
        this.totalCleaners = totalCleaners;
        this.recentIssuances = recentIssuances;
    }

    public int getTotalMaterials() {
        return totalMaterials;
    }

    public int getLowStockCount() {
        return lowStockCount;
    }

    public int getTotalCleaners() {
        return totalCleaners;
    }

    public List<RecentIssuance> getRecentIssuances() {
        return recentIssuances;
    }
}
