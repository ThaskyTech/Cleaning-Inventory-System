package cleanpro.desktopapp.controller;

import cleanpro.desktopapp.model.DashboardSummary;
import cleanpro.desktopapp.service.DashboardService;

public class DashboardController {
    private final DashboardService dashboardService = new DashboardService();

    public DashboardSummary getDashboardSummary() {
        return dashboardService.getDashboardSummary();
    }
}
