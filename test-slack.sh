#!/bin/bash
# Script para probar notificaciones de Slack

SLACK_WEBHOOK="https://hooks.slack.com/services/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"

echo "🧪 Probando notificación de Slack..."

curl -X POST -H "Content-type: application/json" \
  --data '{"text":"🧪 PRUEBA DE NOTIFICACIÓN SLACK\n📋 Proyecto: MS Students Microservice\n✅ Pipeline mejorado con:\n• Notificaciones más informativas\n• SonarCloud más permisivo\n• Manejo de errores mejorado\n⏰ Timestamp: '$(date)'"}' \
  "$SLACK_WEBHOOK"

echo ""
echo "✅ Notificación de prueba enviada!"