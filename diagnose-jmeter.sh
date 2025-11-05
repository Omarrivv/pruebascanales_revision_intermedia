#!/bin/bash

# Script de diagnóstico JMeter
# Este script ejecuta una prueba simple para diagnosticar problemas

echo "🔍 Diagnóstico JMeter para API Students"
echo "=========================================="

# Variables
URL="https://shiny-barnacle-4jq6457gx6pc9g7-8102.app.github.dev/api/v1/students"
JMX_FILE="src/test/jmeter/students-load-test.jmx"
RESULTS_DIR="target/jmeter-results"
JTL_FILE="$RESULTS_DIR/diagnostic-$(date +%Y%m%d-%H%M%S).jtl"

# Crear directorio de resultados
mkdir -p "$RESULTS_DIR"

echo "📍 URL de prueba: $URL"
echo "📁 Directorio de resultados: $RESULTS_DIR"

# Probar conectividad primero
echo ""
echo "🌐 Probando conectividad con curl..."
HTTP_CODE=$(curl -w "%{http_code}" -s -o /tmp/api_response.json -k "$URL")
echo "   Código HTTP: $HTTP_CODE"

if [ "$HTTP_CODE" = "200" ]; then
    echo "   ✅ Conectividad OK"
    echo "   📄 Respuesta:"
    cat /tmp/api_response.json | head -c 200
    echo "..."
else
    echo "   ❌ Error de conectividad (HTTP $HTTP_CODE)"
    exit 1
fi

echo ""
echo ""
echo "🚀 Ejecutando JMeter con configuración mínima..."

# Ejecutar JMeter con configuración mínima
jmeter -n -t "$JMX_FILE" \
    -l "$JTL_FILE" \
    -Jbase.url="$URL" \
    -Jthreads="2" \
    -Jrampup="5" \
    -Jduration="30" \
    -Djmeter.save.saveservice.output_format=xml \
    -Djmeter.save.saveservice.response_data=true \
    -Djmeter.save.saveservice.samplerData=true \
    -Djmeter.save.saveservice.responseHeaders=true \
    -Djmeter.save.saveservice.requestHeaders=true \
    -Djmeter.save.saveservice.assertion_results=all

# Verificar resultados
echo ""
echo "📊 Analizando resultados..."
if [ -f "$JTL_FILE" ]; then
    echo "✅ Archivo de resultados generado: $JTL_FILE"
    
    # Contar errores
    TOTAL_SAMPLES=$(grep -c "<httpSample" "$JTL_FILE" 2>/dev/null || echo "0")
    ERROR_SAMPLES=$(grep 'success="false"' "$JTL_FILE" | wc -l 2>/dev/null || echo "0")
    SUCCESS_SAMPLES=$((TOTAL_SAMPLES - ERROR_SAMPLES))
    
    echo "📈 Estadísticas:"
    echo "   Total de muestras: $TOTAL_SAMPLES"
    echo "   Exitosas: $SUCCESS_SAMPLES"
    echo "   Con errores: $ERROR_SAMPLES"
    
    if [ "$ERROR_SAMPLES" -gt "0" ]; then
        echo ""
        echo "❌ Errores encontrados:"
        echo "------------------------"
        grep -A 5 'success="false"' "$JTL_FILE" | head -20
    else
        echo "   ✅ Todas las pruebas pasaron correctamente"
    fi
    
    # Mostrar códigos de respuesta únicos
    echo ""
    echo "📋 Códigos de respuesta encontrados:"
    grep -o 'rc="[0-9]*"' "$JTL_FILE" | sort | uniq -c 2>/dev/null || echo "   No se pudieron extraer códigos de respuesta"
    
else
    echo "❌ No se generó archivo de resultados"
fi

echo ""
echo "🎯 Diagnóstico completado"
echo "📄 Logs detallados disponibles en: $JTL_FILE"