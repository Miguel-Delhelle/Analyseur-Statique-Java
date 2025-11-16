// dendrogram-d3.ts
import * as d3 from 'd3';

export interface DendroNode {
    leftChild: DendroNode | null;
    rightChild: DendroNode | null;
    hauteurCoupling: number;
    className: string | null;
    leaf: boolean;
}

interface D3HierarchyNode {
    name: string;
    children?: D3HierarchyNode[];
    value: number; // Hauteur
}

export class D3DendrogramRenderer {

    render(root: DendroNode, container: HTMLElement): void {
        container.innerHTML = '';

        const d3Data = this.transformToD3Hierarchy(root);

        const margin = { top: 30, right: 30, bottom: 200, left: 70 };
        const height = 600;

        const tempRoot = d3.hierarchy(d3Data);
        const leafCount = tempRoot.leaves().length;
        const width = leafCount * 40;

        const svg = d3.select(container)
            .append("svg")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
            .append("g")
            .attr("transform", `translate(${margin.left},${margin.top})`);

        const hierarchyRoot = d3.hierarchy(d3Data, d => d.children);

        // Tri pour éviter les croisements (votre méthode est bonne)
        hierarchyRoot.sort((a, b) => a.leaves().length - b.leaves().length);

        const clusterLayout = d3.cluster<D3HierarchyNode>().size([width, height]);
        clusterLayout(hierarchyRoot);

        // --- DÉBUT DE LA MODIFICATION ---

        // 1. TROUVER LE MINIMUM ET MAXIMUM DES HAUTEURS *UNIQUEMENT POUR LES NŒUDS DE FUSION*
        const intermediateNodes = hierarchyRoot.descendants().filter(d => d.children);
        const minIntermediateHeight = d3.min(intermediateNodes, d => d.data.value) ?? 0;
        const maxOverallHeight = d3.max(hierarchyRoot.descendants(), d => d.data.value) ?? 1;

        // 2. DÉFINIR LE DOMAINE DE L'ÉCHELLE Y SELON VOTRE RÈGLE
        const yDomainStart = minIntermediateHeight - 0.1;
        const yScale = d3.scaleLinear().domain([yDomainStart, maxOverallHeight]).range([height, 0]);

        // --- FIN DE LA MODIFICATION ---

        // --- Dessiner les liens ---
        svg.selectAll('path.link')
            .data(hierarchyRoot.descendants().filter(d => d.children)) // On ne dessine que pour les parents
            .enter()
            .append('path')
            .attr('class', 'link')
            .attr('fill', 'none')
            .attr('stroke', '#555')
            .attr('stroke-width', 1.5)
            // Utilisation de la nouvelle fonction de dessin qui gère les feuilles
            .attr('d', d => this.computeLinkPath(d, yScale, height));

        // --- Dessiner les nœuds (labels) ---
        // Cette partie est déjà correcte, elle positionne bien les labels en bas
        svg.selectAll('g.label-group')
            .data(hierarchyRoot.leaves())
            .enter()
            .append('g')
            .attr('class', 'label-group')
            .attr('transform', d => `translate(${d.x},${height + 10})`)
            .append("text")
                .attr("transform", "rotate(65)")
                .style("text-anchor", "start")
                .style("font-size", "11px")
                .text(d => d.data.name.split('.').pop() || '');

        // --- Axe Y ---
        const yAxis = d3.axisLeft(yScale).ticks(10).tickFormat(d3.format(".3f"));
        svg.append("g").call(yAxis);

        svg.append('text')
            .attr('transform', `rotate(-90)`)
            .attr('y', -margin.left + 20)
            .attr('x', -(height / 2))
            .style('text-anchor', 'middle')
            .style('font-size', '12px')
            .style('font-weight', 'bold')
            .text('Distance de Couplage');

        this.setupContainer(container);
    }

    private transformToD3Hierarchy(node: DendroNode): D3HierarchyNode {
        if (node.leaf && node.className) {
            // Pour les feuilles, la valeur est utilisée pour le tri, mais pas pour la position Y
            return { name: node.className, value: node.hauteurCoupling };
        }

        const children: D3HierarchyNode[] = [];
        if (node.leftChild) children.push(this.transformToD3Hierarchy(node.leftChild));
        if (node.rightChild) children.push(this.transformToD3Hierarchy(node.rightChild));

        return { name: `cluster-${node.hauteurCoupling}`, children, value: node.hauteurCoupling };
    }

    /**
     * Calcule le chemin SVG pour un nœud parent.
     * Cette fonction gère correctement les feuilles.
     */
    private computeLinkPath(
        d: d3.HierarchyNode<D3HierarchyNode>,
        yScale: d3.ScaleLinear<number, number>,
        chartHeight: number
    ): string {
        if (!d.children) return '';

        const parentY = yScale(d.data.value);
        const childLeft = d.children[0];
        const childRight = d.children[1];

        // Pour chaque enfant, on détermine sa position Y.
        // Si c'est une feuille, sa position Y est TOUJOURS en bas du graphique (chartHeight).
        // Sinon, c'est la position calculée par l'échelle.
        const leftY = childLeft.children ? yScale(childLeft.data.value) : chartHeight;
        const rightY = childRight.children ? yScale(childRight.data.value) : chartHeight;

        // On dessine le chemin en "U"
        return `M${childLeft.x},${leftY} L${childLeft.x},${parentY} L${childRight.x},${parentY} L${childRight.x},${rightY}`;
    }

    private setupContainer(container: HTMLElement): void {
        const svgElement = container.querySelector('svg');
        if (!svgElement) return;

        const scrollContainer = document.createElement('div');
        scrollContainer.style.overflowX = 'auto';
        scrollContainer.style.width = '100%';
        scrollContainer.style.border = '1px solid #ddd';
        scrollContainer.style.backgroundColor = '#fdfdfd';
        scrollContainer.appendChild(svgElement);

        container.innerHTML = '';
        container.appendChild(scrollContainer);
    }
}