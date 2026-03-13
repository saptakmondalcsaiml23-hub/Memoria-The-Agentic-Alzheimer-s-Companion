{{- define "memoria.name" -}}
memoria
{{- end -}}

{{- define "memoria.labels" -}}
app.kubernetes.io/name: {{ include "memoria.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}
