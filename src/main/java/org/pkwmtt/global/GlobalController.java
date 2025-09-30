package org.pkwmtt.global;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/global/metrics")
public class GlobalController {
    @GetMapping("")
    public String matrics(){
        return """
          <!doctype html>
          <html lang="en">
          <head>
            <meta charset="utf-8">
            <title>Prometheus metrics viewer</title>
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <style>
              body { font-family: system-ui, sans-serif; margin: 20px; }
              header { display: flex; gap: 12px; align-items: center; margin-bottom: 12px; }
              input[type="text"] { width: 420px; }
              pre { background: #111; color: #eee; padding: 12px; border-radius: 6px; overflow: auto; max-height: 60vh; }
              table { border-collapse: collapse; width: 100%; margin-top: 16px; }
              th, td { border: 1px solid #ddd; padding: 8px; }
              th { background: #f7f7f7; text-align: left; }
              .muted { color: #666; }
            </style>
          </head>
          <body>
            <header>
              <label for="url"><strong>Endpoint:</strong></label>
              <input id="url" type="text" value="/actuator/prometheus">
              <label for="interval"><strong>Refresh (s):</strong></label>
              <input id="interval" type="number" min="0" value="5" style="width: 80px;">
              <button id="refresh"><strong>Fetch now</strong></button>
              <span id="status" class="muted"></span>
            </header>
          
            <section>
              <h3>Raw metrics (Prometheus exposition)</h3>
              <pre id="raw">Loading…</pre>
            </section>
          
            <section>
              <h3>Quick values (parsed)</h3>
              <table id="parsed">
                <thead>
                  <tr>
                    <th>Metric</th>
                    <th>Labels</th>
                    <th>Value</th>
                  </tr>
                </thead>
                <tbody></tbody>
              </table>
              <p class="muted">This simple parser shows selected counters and gauges; raw text above is canonical.</p>
            </section>
          
            <script>
              const urlInput = document.getElementById('url');
              const intervalInput = document.getElementById('interval');
              const statusEl = document.getElementById('status');
              const rawEl = document.getElementById('raw');
              const tableBody = document.querySelector('#parsed tbody');
              const selectedMetrics = [
                'http_server_requests_seconds_count', // Spring Boot Micrometer
                'http_server_requests_seconds_sum',
                'http_server_requests_seconds_max',
                'jvm_memory_used_bytes',
                'process_uptime_seconds'
              ];
          
              let timer = null;
          
              async function fetchMetrics() {
                const endpoint = urlInput.value.trim() || '/actuator/prometheus';
                statusEl.textContent = 'Fetching…';
                try {
                  const res = await fetch(endpoint, { cache: 'no-store' });
                  if (!res.ok) throw new Error(res.status + ' ' + res.statusText);
                  const text = await res.text();
                  rawEl.textContent = text;
                  statusEl.textContent = 'Updated: ' + new Date().toLocaleTimeString();
                  renderParsed(text);
                } catch (err) {
                  statusEl.textContent = 'Error: ' + err.message;
                  rawEl.textContent = '';
                  tableBody.innerHTML = '';
                }
              }
          
              function renderParsed(text) {
                tableBody.innerHTML = '';
                const lines = text.split('\\n').filter(l => l && !l.startsWith('#'));
                for (const line of lines) {
                  // Example line: metric_name{label="value",...} 123.4
                  const spaceIdx = line.lastIndexOf(' ');
                  if (spaceIdx < 0) continue;
                  const left = line.slice(0, spaceIdx);
                  const value = line.slice(spaceIdx + 1);
                  let name = left;
                  let labels = '';
                  const braceIdx = left.indexOf('{');
                  if (braceIdx >= 0) {
                    name = left.slice(0, braceIdx);
                    labels = left.slice(braceIdx + 1, left.lastIndexOf('}'));
                  }
                  if (!selectedMetrics.includes(name)) continue;
          
                  const tr = document.createElement('tr');
                  const tdName = document.createElement('td');
                  tdName.textContent = name;
                  const tdLabels = document.createElement('td');
                  tdLabels.textContent = labels;
                  const tdValue = document.createElement('td');
                  tdValue.textContent = value;
                  tr.append(tdName, tdLabels, tdValue);
                  tableBody.appendChild(tr);
                }
                if (!tableBody.children.length) {
                  const tr = document.createElement('tr');
                  const td = document.createElement('td');
                  td.colSpan = 3;
                  td.textContent = 'No selected metrics found. Adjust the endpoint or selectedMetrics list.';
                  tr.appendChild(td);
                  tableBody.appendChild(tr);
                }
              }
          
              function startAutoRefresh() {
                const seconds = Number(intervalInput.value);
                if (timer) clearInterval(timer);
                if (seconds > 0) {
                  timer = setInterval(fetchMetrics, seconds * 1000);
                }
              }
          
              document.getElementById('refresh').addEventListener('click', fetchMetrics);
              intervalInput.addEventListener('change', startAutoRefresh);
              urlInput.addEventListener('change', fetchMetrics);
          
              // Initial load
              fetchMetrics();
              startAutoRefresh();
            </script>
          </body>
          </html>
          
          """;
    }
}
