import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.*;
import oracle.jdbc.OracleTypes;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== PRUEBA DE CONEXIÓN A ORACLE ===");
        System.out.println("Parámetros de conexión:");
        System.out.println("URL: " + ConexionBD.getUrl());
        System.out.println("Usuario: " + ConexionBD.getUser());

        try (Connection conn = ConexionBD.getConnection()) {
            // Verificación 1: Conexión establecida
            System.out.println("\n1. Conexión establecida con éxito");

            // Verificación 2: Obtener metadatos de la base de datos
            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println("\n2. Información de la base de datos:");
            System.out.println("  • Producto: " + metaData.getDatabaseProductName());
            System.out.println("  • Versión: " + metaData.getDatabaseProductVersion());
            System.out.println("  • Driver: " + metaData.getDriverName() + " v" + metaData.getDriverVersion());

            // Verificación 3: Validar conexión activa
            System.out.println("\n3. Validación final:");
            if (conn.isValid(2)) {
                System.out.println("  • La conexión es válida y activa");
                System.out.println("  • ¡Todas las verificaciones fueron exitosas!");
            } else {
                System.out.println("  • Advertencia: La conexión no parece válida");
            }

            //Ejemplo cursor explicito
            System.out.println("\n=== EJEMPLO DE CURSOR EXPLÍCITO ===");
            System.out.println("Llamando a obtenerContratosPorCliente...");

            int clienteId = 1; // ID de cliente a consultar

            try (CallableStatement stmt = conn.prepareCall("{ call obtenerContratosPorCliente(?, ?) }")) {
                // 1. Establecer parámetro de entrada
                stmt.setInt(1, clienteId);

                // 2. Registrar parámetro de salida como cursor
                stmt.registerOutParameter(2, OracleTypes.CURSOR);

                // 3. Ejecutar el procedimiento
                stmt.execute();

                // 4. Obtener el ResultSet del cursor
                try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                    System.out.println("\nContratos del cliente ID " + clienteId + ":");
                    System.out.println("----------------------------------");

                    // 5. Procesar resultados del cursor explícito
                    while (rs.next()) {
                        System.out.println(
                                "Contrato ID: " + rs.getInt("contrato_id") +
                                        "\nDirección: " + rs.getString("direccion_propiedad") +
                                        "\nValor renta: $" + rs.getDouble("valor_renta") +
                                        "\nPeríodo: " + rs.getDate("fecha_inicio") +
                                        " hasta " + rs.getDate("fecha_fin") +
                                        "\n----------------------------------");
                    }

                    if (rs.getRow() == 0) {
                        System.out.println("El cliente no tiene contratos registrados.");
                    }
                }
            }

            // Ejemplo cursor implicito
            System.out.println("\n=== EJEMPLO DE CURSOR IMPLÍCITO ===");
            System.out.println("Llamando a obtenerPagosPorMetodo...");

            int metodoPagoId = 3; // ID del método de pago a consultar

            try (CallableStatement stmt = conn.prepareCall("{ ? = call obtenerPagosPorMetodo(?) }")) {
                // 1. Registrar parámetro de retorno como cursor
                stmt.registerOutParameter(1, OracleTypes.CURSOR);

                // 2. Establecer parámetro de entrada
                stmt.setInt(2, metodoPagoId);

                // 3. Ejecutar la función
                stmt.execute();

                // 4. Obtener el ResultSet del cursor
                try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                    System.out.println("\nPagos con método ID " + metodoPagoId + ":");
                    System.out.println("----------------------------------");

                    // 5. Procesar resultados del cursor implícito
                    while (rs.next()) {
                        System.out.println(
                                "Pago ID: " + rs.getInt("pago_id") +
                                        "\nMétodo: " + rs.getString("nombre_metodo") +
                                        "\nDetalles: " + rs.getString("detalles_pago") +
                                        "\nContrato ID: " + rs.getInt("contrato_id") +
                                        "\nValor renta: $" + rs.getDouble("valor_renta") +
                                        "\n----------------------------------");
                    }

                    if (rs.getRow() == 0) {
                        System.out.println("No se encontraron pagos con este método.");
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("\n❌ ERROR EN LA CONEXIÓN:");
            System.out.println("Mensaje: " + e.getMessage());
            System.out.println("Código SQL: " + e.getErrorCode());

            // Diagnóstico de errores comunes
            if (e.getMessage().contains("ORA-01017")) {
                System.out.println("\nPosible problema: Credenciales incorrectas");
            } else if (e.getMessage().contains("ORA-12505")) {
                System.out.println("\nPosible problema: SID incorrecto (¿Es 'INGSIS' correcto?)");
                System.out.println("Prueba con: jdbc:oracle:thin:@//orion.javeriana.edu.co:1521/LABORATORIO");
            } else if (e.getMessage().contains("ORA-12170")) {
                System.out.println("\nPosible problema: No se puede alcanzar el servidor");
                System.out.println("Verifica:");
                System.out.println("- Que el servidor 10.34.1.33 esté accesible");
                System.out.println("- Que el puerto 1521 no esté bloqueado por firewall");
            }
        } finally {
            System.out.println("\n=== PRUEBA COMPLETADA ===");
        }
    }
}

