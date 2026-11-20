import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

import java.io.PrintWriter;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

/** Corre todas las pruebas del paquete dominio desde la consola (lo usa probar.bat). */
public class EjecutarPruebas {

    public static void main(String[] args) {
        LauncherDiscoveryRequest solicitud = LauncherDiscoveryRequestBuilder.request()
            .selectors(selectPackage("dominio"))
            .build();
        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener resumen = new SummaryGeneratingListener();
        launcher.execute(solicitud, resumen);
        TestExecutionSummary s = resumen.getSummary();
        PrintWriter salida = new PrintWriter(System.out);
        s.printFailuresTo(salida);
        salida.flush();
        System.out.printf("Pruebas: %d  Exitosas: %d  Fallidas: %d%n",
            s.getTestsFoundCount(), s.getTestsSucceededCount(), s.getTotalFailureCount());
        System.exit(s.getTotalFailureCount() == 0 ? 0 : 1);
    }
}
