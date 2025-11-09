// DANS : src/main.ts (Remplacez tout le contenu)

import * as d3 from 'd3';

// =================================================================================
// 1. DÉFINITION DES TYPES
// =================================================================================
interface Edge { calleeSignature: string; type: 'CALL' | 'INSTANTIATION' | 'THROWS'; }
interface GraphDTO { adjacencyList: { [callerSignature: string]: Edge[]; }; }
interface AnalysisResponse { metricsDto: { [key: string]: any }; graphDTO: GraphDTO; basePackage: string; }

interface GraphNode extends d3.SimulationNodeDatum { id: string; group: string; packageName: string; }
interface GraphLink extends d3.SimulationLinkDatum<GraphNode> { type: 'CALL' | 'INSTANTIATION' | 'THROWS'; }

type ViewType = 'force' | 'arc' | 'matrix';

// =================================================================================
// 2. RÉFÉRENCES AUX ÉLÉMENTS DU DOM
// =================================================================================
const gitUrlInput = document.getElementById('gitUrlInput') as HTMLInputElement;
const analyzeGitBtn = document.getElementById('analyzeGitBtn') as HTMLButtonElement;
const zipFileInput = document.getElementById('zipFileInput') as HTMLInputElement;
const analyzeZipBtn = document.getElementById('analyzeZipBtn') as HTMLButtonElement;
const resultsSection = document.getElementById('results') as HTMLElement;
const loader = document.getElementById('loader') as HTMLElement;
const resultsContent = document.getElementById('results-content') as HTMLElement;
const metricsTableBody = document.getElementById('metricsTableBody') as HTMLElement;
const graphContainer = document.getElementById('graphContainer') as HTMLElement;
const filterCallCheckbox = document.getElementById('filter-call') as HTMLInputElement;
const filterInstantiationCheckbox = document.getElementById('filter-instantiation') as HTMLInputElement;
const filterThrowsCheckbox = document.getElementById('filter-throws') as HTMLInputElement;
const filterInternalCheckbox = document.getElementById('filter-internal') as HTMLInputElement;
const fullscreenBtn = document.getElementById('fullscreen-btn') as HTMLButtonElement;
const viewSelector = document.getElementById('view-selector') as HTMLElement;
const viewButtons = viewSelector.querySelectorAll('.view-btn');

// =================================================================================
// 3. STOCKAGE DES DONNÉES ET ÉTATS GLOBAUX
// =================================================================================
let fullNodes: GraphNode[] = [];
let fullLinks: GraphLink[] = [];
let basePackage: string = '';
let currentView: ViewType = 'force';

// =================================================================================
// 4. GESTIONNAIRES D'ÉVÉNEMENTS
// =================================================================================
analyzeGitBtn.addEventListener('click', () => {
    const uriGit = gitUrlInput.value;
    if (!uriGit) return alert("Veuillez fournir une URL de dépôt Git.");
    fetchAnalysisResults('/api/analyses/git', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ uriGit }) });
});

analyzeZipBtn.addEventListener('click', () => {
    const file = zipFileInput.files?.[0];
    if (!file) return alert("Veuillez sélectionner un fichier .zip.");
    const formData = new FormData();
    formData.append('file', file);
    fetchAnalysisResults('/api/analyses/zip', { method: 'POST', body: formData });
});

viewButtons.forEach(button => {
    button.addEventListener('click', () => {
        currentView = button.getAttribute('data-view') as ViewType;
        viewButtons.forEach(btn => btn.classList.remove('active'));
        button.classList.add('active');
        updateVisualization();
    });
});

[filterCallCheckbox, filterInstantiationCheckbox, filterThrowsCheckbox, filterInternalCheckbox].forEach(checkbox => {
    checkbox.addEventListener('change', updateVisualization);
});

fullscreenBtn.addEventListener('click', () => {
    graphContainer.classList.toggle('fullscreen');
    fullscreenBtn.textContent = graphContainer.classList.contains('fullscreen') ? 'Quitter le plein écran' : 'Plein écran';
    setTimeout(updateVisualization, 0); 
});

document.addEventListener('keydown', (event) => {
    if (event.key === 'Escape' && graphContainer.classList.contains('fullscreen')) {
        fullscreenBtn.click();
    }
});

// =================================================================================
// 5. FONCTIONS LOGIQUES
// =================================================================================
async function fetchAnalysisResults(url: string, options: RequestInit) {
    resultsSection.classList.remove('hidden');
    resultsContent.classList.add('hidden');
    loader.classList.remove('hidden');
    try {
        const response = await fetch(url, options);
        if (!response.ok) throw new Error(`Erreur du serveur : ${response.status} ${response.statusText}`);
        const data: AnalysisResponse = await response.json();
        
        basePackage = data.basePackage;
        
        displayMetrics(data.metricsDto);
        resultsContent.classList.remove('hidden');
        displayGraph(data.graphDTO);
    } catch (error) {
        console.error("Erreur lors de l'analyse:", error);
        alert("Une erreur est survenue. Vérifiez la console pour plus de détails.");
        resultsSection.classList.add('hidden');
    } finally {
        loader.classList.add('hidden');
    }
}

function displayMetrics(metrics: { [key: string]: any }) {
    metricsTableBody.innerHTML = '';
    for (const [key, value] of Object.entries(metrics)) {
        const row = document.createElement('tr');
        row.innerHTML = `<td>${key}</td><td>${typeof value === 'number' ? value.toFixed(2) : value}</td>`;
        metricsTableBody.appendChild(row);
    }
}

function transformAdjacencyList(adjacencyList: { [key: string]: Edge[] }): { nodes: GraphNode[], links: GraphLink[] } {
    const nodesMap = new Map<string, GraphNode>();
    const links: GraphLink[] = [];
    for (const callerSignature in adjacencyList) {
        if (!nodesMap.has(callerSignature)) {
            const className = callerSignature.split('#')[0];
            const packageName = className.substring(0, className.lastIndexOf('.')) || '[default]';
            nodesMap.set(callerSignature, { id: callerSignature, group: className, packageName });
        }
        for (const edge of adjacencyList[callerSignature]) {
            if (!nodesMap.has(edge.calleeSignature)) {
                const className = edge.calleeSignature.split('#')[0];
                const packageName = className.substring(0, className.lastIndexOf('.')) || '[default]';
                nodesMap.set(edge.calleeSignature, { id: edge.calleeSignature, group: className, packageName });
            }
            links.push({ source: callerSignature, target: edge.calleeSignature, type: edge.type });
        }
    }
    return { nodes: Array.from(nodesMap.values()), links };
}

function displayGraph(graphData: GraphDTO) {
    const { nodes, links } = transformAdjacencyList(graphData.adjacencyList);
    fullNodes = nodes;
    fullLinks = links;
    updateVisualization();
}

function simplifySignature(signature: string): string {
    const parts = signature.split('#');
    if (parts.length < 2) return signature;
    return parts[1].replace(/\(.*\)/, '(...)');
}

function getNodeId(node: string | number | GraphNode): string {
    if (typeof node === 'string') return node;
    if (typeof node === 'object' && node !== null && 'id' in node) return node.id;
    return '';
}

function updateVisualization() {
    graphContainer.innerHTML = '';

    let linksToRender = fullLinks.filter(link => 
        (filterCallCheckbox.checked && link.type === 'CALL') ||
        (filterInstantiationCheckbox.checked && link.type === 'INSTANTIATION') ||
        (filterThrowsCheckbox.checked && link.type === 'THROWS')
    );

    if (filterInternalCheckbox.checked && basePackage) {
        linksToRender = linksToRender.filter(link => {
            const sourceId = getNodeId(link.source);
            const targetId = getNodeId(link.target);
            return sourceId.startsWith(basePackage) && targetId.startsWith(basePackage);
        });
    }

    const visibleNodeIds = new Set<string>();
    linksToRender.forEach(link => {
        visibleNodeIds.add(getNodeId(link.source));
        visibleNodeIds.add(getNodeId(link.target));
    });
    const nodesToRender = fullNodes.filter(node => visibleNodeIds.has(node.id));

    if (nodesToRender.length === 0) {
        graphContainer.innerHTML = '<p style="text-align: center; padding: 2em; color: var(--text-muted-color);">Aucun élément à afficher avec les filtres actuels.</p>';
        return;
    }

    switch (currentView) {
        case 'force':
            renderForceDirectedGraph(nodesToRender, linksToRender);
            break;
        case 'arc':
            renderArcDiagram(nodesToRender, linksToRender);
            break;
        case 'matrix':
            renderAdjacencyMatrix(nodesToRender, linksToRender);
            break;
    }
}

function renderForceDirectedGraph(nodes: GraphNode[], links: GraphLink[]) {
    const width = graphContainer.clientWidth;
    const height = graphContainer.clientHeight || 600;

    const svg = d3.select(graphContainer).append("svg").attr("viewBox", [-width / 2, -height / 2, width, height]);
    const containerGroup = svg.append("g");
    const zoom = d3.zoom<SVGSVGElement, any>().scaleExtent([0.05, 10]).on("zoom", e => containerGroup.attr("transform", e.transform));
    svg.call(zoom);

    svg.append("defs").append("marker")
        .attr("id", "arrowhead").attr("viewBox", "0 -5 10 10").attr("refX", 25)
        .attr("markerWidth", 6).attr("markerHeight", 6).attr("orient", "auto")
      .append("path").attr("d", "M0,-5L10,0L0,5").attr("class", "arrow-marker");
      
    const packageColor = d3.scaleOrdinal(d3.schemeTableau10);
    const uniquePackages = Array.from(new Set(nodes.map(n => n.packageName)));
    const angleScale = d3.scaleBand().domain(uniquePackages).range([0, 2 * Math.PI]);
    
    const packageCenters = new Map(uniquePackages.map(p => {
        const angle = angleScale(p) || 0;
        const radius = Math.min(width, height) * 0.4;
        return [p, { x: Math.cos(angle) * radius, y: Math.sin(angle) * radius }];
    }));

    const nodeDegrees = new Map<string, number>();
    links.forEach(link => {
        const sourceId = getNodeId(link.source);
        const targetId = getNodeId(link.target);
        nodeDegrees.set(sourceId, (nodeDegrees.get(sourceId) || 0) + 1);
        nodeDegrees.set(targetId, (nodeDegrees.get(targetId) || 0) + 1);
    });
    const radiusScale = d3.scaleSqrt().domain([0, d3.max(Array.from(nodeDegrees.values())) || 1]).range([5, 18]);
    
    const simulation = d3.forceSimulation(nodes)
        .force("link", d3.forceLink<GraphNode, GraphLink>(links).id(d => d.id).distance(80).strength(0.2))
        .force("charge", d3.forceManyBody().strength(-250))
        .force("x", d3.forceX<GraphNode>(d => packageCenters.get(d.packageName)?.x || 0).strength(0.03))
        .force("y", d3.forceY<GraphNode>(d => packageCenters.get(d.packageName)?.y || 0).strength(0.03))
        .force("collide", d3.forceCollide<GraphNode>(d => radiusScale(nodeDegrees.get(d.id) || 1) + 5));

    const groups = d3.group(nodes, d => d.group);
    const groupPath = containerGroup.append("g").attr("class", "group-layer")
        .selectAll(".group-box").data(groups).join("path")
        .attr("class", "group-box")
        .attr("stroke", d => packageColor(d[1][0].packageName))
        .attr("fill", d => packageColor(d[1][0].packageName));

    const linkElements = containerGroup.append("g").attr("class", "link-layer").selectAll("line").data(links).join("line")
        .attr("stroke-width", 1.5).attr("stroke-opacity", 0.6).attr("stroke", "#999")
        .attr("marker-end", "url(#arrowhead)");
    
    const nodeElements = containerGroup.append("g").attr("class", "node-layer").selectAll("g").data(nodes).join("g")
        .call(d3.drag<any, GraphNode>().on("start", dragstarted).on("drag", dragged).on("end", dragended));

    nodeElements.append("circle")
        .attr("r", d => radiusScale(nodeDegrees.get(d.id) || 1))
        .attr("fill", d => packageColor(d.packageName))
        .attr("stroke", d => d3.color(packageColor(d.packageName))?.brighter(1).toString() || "#fff")
        .attr("stroke-width", 2);

    nodeElements.append("text").attr("x", d => radiusScale(nodeDegrees.get(d.id) || 1) + 5).attr("y", "0.31em").attr("class", "node-label").style("display", "none").text(d => simplifySignature(d.id));
    nodeElements.append("title").text(d => d.id);
    
    nodeElements
    //@ts-ignore
        .on('mouseover', function(event, d) {
            d3.select(this).select('text').style('display', 'block');
            const connectedNodeIds = new Set<string>([d.id]);
            links.forEach(link => {
                const sourceId = getNodeId(link.source);
                const targetId = getNodeId(link.target);
                if (sourceId === d.id) connectedNodeIds.add(targetId);
                if (targetId === d.id) connectedNodeIds.add(sourceId);
            });
            nodeElements.style('opacity', n => connectedNodeIds.has(n.id) ? 1.0 : 0.1);
            linkElements.style('opacity', l => (getNodeId(l.source) === d.id || getNodeId(l.target) === d.id) ? 0.9 : 0.05);
            groupPath.style('fill-opacity', g => g[0] === d.group ? 0.2 : 0.05);
        })
        .on('mouseout', function() {
            d3.select(this).select('text').style('display', 'none');
            nodeElements.style('opacity', 1.0);
            linkElements.style('opacity', 0.6);
            groupPath.style('fill-opacity', 0.1);
        });

    simulation.on("tick", () => {
        linkElements.attr("x1", d => (d.source as GraphNode).x!).attr("y1", d => (d.source as GraphNode).y!)
            .attr("x2", d => (d.target as GraphNode).x!).attr("y2", d => (d.target as GraphNode).y!);
        nodeElements.attr("transform", d => `translate(${d.x},${d.y})`);
        
        groups.forEach(groupNodes => {
            const hull = d3.polygonHull(groupNodes.map(d => [d.x!, d.y!]));
            if (hull) {
                const padding = 20 + (d3.mean(groupNodes, d => radiusScale(nodeDegrees.get(d.id) || 1)) || 5);
                const centroid = d3.polygonCentroid(hull);
                hull.forEach(point => {
                    const angle = Math.atan2(point[1] - centroid[1], point[0] - centroid[0]);
                    point[0] += Math.cos(angle) * padding;
                    point[1] += Math.sin(angle) * padding;
                });
                groupPath.filter(d => d[0] === groupNodes[0].group).attr("d", `M${hull.join("L")}Z`);
            }
        });
    });

    function dragstarted(event: any, d: GraphNode) {
        if (!event.active) simulation.alphaTarget(0.3).restart();
        d.fx = d.x; d.fy = d.y;
    }
    function dragged(event: any, d: GraphNode) {
        d.fx = event.x; d.fy = event.y;
    }
    function dragended(event: any, d: GraphNode) {
        if (!event.active) simulation.alphaTarget(0);
        d.fx = null; d.fy = null;
    }
}

function renderArcDiagram(nodes: GraphNode[], links: GraphLink[]) {
    const margin = { top: 20, right: 30, bottom: 180, left: 40 };
    const width = graphContainer.clientWidth;
    const height = Math.max(500, width / 3);
    
    const svg = d3.select(graphContainer).append("svg")
        .attr("viewBox", [0, 0, width, height]);

    // Trier les nœuds pour les regrouper visuellement
    const sortedNodes = [...nodes].sort((a, b) => d3.ascending(a.group, b.group) || d3.ascending(a.id, b.id));
    const nodeIds = sortedNodes.map(d => d.id);
    
    const x = d3.scalePoint()
        .domain(nodeIds)
        .range([margin.left, width - margin.right])
        .padding(0.5);

    const packageColor = d3.scaleOrdinal(d3.schemeTableau10).domain(nodes.map(d => d.packageName));

    // Dessiner les liens (arcs)
    const linkElements = svg.append("g")
        .attr("fill", "none")
        .attr("stroke-opacity", 0.4)
        .selectAll("path")
        .data(links)
        .join("path")
        .attr("stroke", d => d.type === 'THROWS' ? '#d9534f' : d.type === 'INSTANTIATION' ? '#5cb85c' : '#999')
        .attr("d", d => {
            const sourceX = x(getNodeId(d.source))!;
            const targetX = x(getNodeId(d.target))!;
            const r = Math.abs(sourceX - targetX) / 2;
            return `M ${sourceX},${height - margin.bottom} A ${r},${r} 0 0,1 ${targetX},${height - margin.bottom}`;
        });

    // Dessiner les nœuds
    const nodeElements = svg.append("g")
        .selectAll("g")
        .data(sortedNodes)
        .join("g")
        .attr("transform", d => `translate(${x(d.id)}, ${height - margin.bottom})`);

    nodeElements.append("circle")
        .attr("r", 5)
        .attr("fill", d => packageColor(d.packageName));

    nodeElements.append("text")
        .text(d => simplifySignature(d.id))
        .attr("transform", "rotate(-90)")
        .attr("dy", "0.35em")
        .attr("x", -10)
        .style("text-anchor", "end")
        .style("font-size", "10px")
        .style("fill", "var(--text-muted-color)");
        
    nodeElements.append("title").text(d => d.id);

    // Interactivité au survol
    nodeElements
    //@ts-ignore
        .on('mouseover', (event, d) => {
            nodeElements.style('opacity', n => n.id === d.id ? 1.0 : 0.2);
            linkElements.style('opacity', l => getNodeId(l.source) === d.id || getNodeId(l.target) === d.id ? 0.9 : 0.05);
        })
        .on('mouseout', () => {
            nodeElements.style('opacity', 1.0);
            linkElements.style('opacity', 0.4);
        });
}

function renderAdjacencyMatrix(nodes: GraphNode[], links: GraphLink[]) {
    const margin = { top: 150, right: 10, bottom: 10, left: 150 };
    const width = graphContainer.clientWidth;
    const height = width; // La matrice est carrée

    const svg = d3.select(graphContainer).append("svg")
        .attr("viewBox", [0, 0, width, height]);

    // Trier les nœuds pour regrouper les packages/classes
    const sortedNodes = [...nodes].sort((a, b) => d3.ascending(a.packageName, b.packageName) || d3.ascending(a.group, b.group) || d3.ascending(a.id, b.id));
    const nodeIds = sortedNodes.map(d => d.id);

    const x = d3.scaleBand().domain(nodeIds).range([margin.left, width - margin.right]).padding(0.1);
    const y = d3.scaleBand().domain(nodeIds).range([margin.top, height - margin.bottom]).padding(0.1);

    const packageColor = d3.scaleOrdinal(d3.schemeTableau10).domain(nodes.map(d => d.packageName));
    const linkColor = d3.scaleOrdinal<string, string>().domain(['CALL', 'INSTANTIATION', 'THROWS']).range(['#999', '#5cb85c', '#d9534f']);

    // Créer un lookup rapide pour les liens
    //@ts-ignore
    const linkSet = new Set(links.map(l => `${getNodeId(l.source)}->${getNodeId(l.target)}`));

    // Labels des lignes et colonnes
    const rowLabels = svg.append("g")
        .selectAll("text")
        .data(sortedNodes)
        .join("text")
        .attr("transform", d => `translate(${margin.left - 5}, ${y(d.id)! + y.bandwidth() / 2})`)
        .text(d => simplifySignature(d.id))
        .style("text-anchor", "end")
        .style("font-size", "8px")
        .style("fill", d => packageColor(d.packageName));

    const colLabels = svg.append("g")
        .selectAll("text")
        .data(sortedNodes)
        .join("text")
        .attr("transform", d => `translate(${x(d.id)! + x.bandwidth() / 2}, ${margin.top - 5}) rotate(-90)`)
        .text(d => simplifySignature(d.id))
        .style("text-anchor", "start")
        .style("font-size", "8px")
        .style("fill", d => packageColor(d.packageName));
    
    // Dessiner les cellules de la matrice
    const cells = svg.append("g")
        .selectAll("rect")
        .data(links)
        .join("rect")
        .attr("x", d => x(getNodeId(d.target))!)
        .attr("y", d => y(getNodeId(d.source))!)
        .attr("width", x.bandwidth())
        .attr("height", y.bandwidth())
        .attr("fill", d => linkColor(d.type))
        .append("title")
        .text(d => `${simplifySignature(getNodeId(d.source))} -> ${simplifySignature(getNodeId(d.target))}`);

    // Interactivité au survol
    //@ts-ignore
    const highlight = (event: any, d: GraphNode) => {
        rowLabels.style("font-weight", r => r.id === d.id ? "bold" : "normal");
        colLabels.style("font-weight", c => c.id === d.id ? "bold" : "normal");
        cells.filter(l => getNodeId(l.source) === d.id || getNodeId(l.target) === d.id)
             .attr("stroke", "white").attr("stroke-width", 1.5);
    };

    const unhighlight = () => {
        rowLabels.style("font-weight", "normal");
        colLabels.style("font-weight", "normal");
        cells.attr("stroke", "none");
    };

    rowLabels.on("mouseover", highlight).on("mouseout", unhighlight);
    colLabels.on("mouseover", highlight).on("mouseout", unhighlight);
}
