@echo off
echo ============================================
echo Testing LogiTrack Automatic Auditing System
echo ============================================
echo.

echo 1. Testing Product Creation (should trigger INSERT audit):
echo curl -X POST http://localhost:8080/api/productos ^
echo   -H "Content-Type: application/json" ^
echo   -d "{\"nombre\":\"Laptop Dell\",\"categoria\":\"Electrónica\",\"stock\":50,\"precio\":1500.00}"
echo.

echo 2. Testing Product Update (should trigger UPDATE audit):
echo curl -X PUT http://localhost:8080/api/productos/1 ^
echo   -H "Content-Type: application/json" ^
echo   -d "{\"nombre\":\"Laptop Dell Updated\",\"categoria\":\"Electrónica\",\"stock\":45,\"precio\":1450.00}"
echo.

echo 3. Testing Product Deletion (should trigger DELETE audit):
echo curl -X DELETE http://localhost:8080/api/productos/1
echo.

echo 4. Check Audit Trail:
echo curl -X GET http://localhost:8080/api/auditoria
echo.

echo 5. Check Audits by Operation Type:
echo curl -X GET http://localhost:8080/api/auditoria/tipo/INSERT
echo curl -X GET http://localhost:8080/api/auditoria/tipo/UPDATE
echo curl -X GET http://localhost:8080/api/auditoria/tipo/DELETE
echo.
echo ============================================
echo Note: Make sure the application is running on port 8080
echo The auditing system will automatically track all operations!
echo ============================================