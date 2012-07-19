<%!@javax.inject.Inject
    private PollerManager manager ;%><html>
<body>
    <h1>Infinispan State</h1>
    <%= manager.getInfoHTML() %>
    <h1>JSON</h1>
    <pre>
        <%= manager.getInfoJSON() %>
    </pre>
</body>
</html>
