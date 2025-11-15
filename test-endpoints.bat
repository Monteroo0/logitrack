@echo off
echo ============================================
echo Testing LogiTrack Advanced Queries & Reports
echo ============================================
echo.

echo 1. Testing Stock Low Endpoint:
echo curl -X GET http://localhost:8080/api/productos/stock-bajo
echo.

echo 2. Testing Date Range Movements Endpoint:
echo curl -X GET "http://localhost:8080/api/movimientos/por-fecha?inicio=2024-01-01&fin=2024-12-31"
echo.

echo 3. Testing User Audit Endpoint:
echo curl -X GET http://localhost:8080/api/auditoria/usuario/admin
echo.

echo 4. Testing General Report Endpoint:
echo curl -X GET http://localhost:8080/api/reportes/resumen
echo.

echo 5. Testing General Report with Specific Warehouse:
echo curl -X GET "http://localhost:8080/api/reportes/resumen?bodega=Central"
echo.
echo ============================================
echo Note: Make sure the application is running on port 8080
echo ============================================