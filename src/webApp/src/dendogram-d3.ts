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

        // --- Layout D3 ---
        const hierarchyRoot = d3.hierarchy(d3Data, d => d.children);

        // Tri des enfants pour éviter les croisements
        hierarchyRoot.each(d => {
            if (d.children) {
                d.children.sort((a, b) => a.leaves().length - b.leaves().length);
            }
        });

        const clusterLayout = d3.cluster<D3HierarchyNode>().size([width, height]);
        clusterLayout(hierarchyRoot);

        const maxHeight = d3.max(hierarchyRoot.descendants(), d => d.data.value)!;
        const yScale = d3.scaleLinear().domain([0, maxHeight]).range([height, 0]);

        // --- Dessiner les liens ---
        svg.selectAll('path.link')
            .data(hierarchyRoot.descendants().filter(d => d.children).reverse())
            .enter()
            .append('path')
            .attr('class', 'link')
            .attr('fill', 'none')
            .attr('stroke', '#555')
            .attr('stroke-width', 1.5)
            .attr('d', d => this.computeLinkPath(d as d3.HierarchyPointNode<D3HierarchyNode>));

        // --- Dessiner les nœuds (labels) ---
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
            .text('Hauteur de Couplage');

        // --- Conteneur scrollable ---
        this.setupContainer(container);
    }

    private transformToD3Hierarchy(node: DendroNode): D3HierarchyNode {
        if (node.leaf && node.className) {
            return { name: node.className, value: node.hauteurCoupling };
        }

        const children: D3HierarchyNode[] = [];
        if (node.leftChild) children.push(this.transformToD3Hierarchy(node.leftChild));
        if (node.rightChild) children.push(this.transformToD3Hierarchy(node.rightChild));

        return {
            name: `cluster-${node.hauteurCoupling}`,
            children,
            value: node.hauteurCoupling
        };
    }

    private computeLinkPath(d: d3.HierarchyPointNode<D3HierarchyNode>): string {
    if (!d.children || d.children.length === 0) return '';

    // Cas 2 enfants : simple U-shape
    if (d.children.length === 2) {
        const [left, right] = d.children;
        return `M${left.x},${left.y} L${left.x},${d.y} L${right.x},${d.y} L${right.x},${right.y}`;
    }

    // Cas général pour 3 enfants ou plus
    const xs = d.children.map(c => c.x);
    const minX = Math.min(...xs);
    const maxX = Math.max(...xs);

    // Commence au premier enfant
    let path = `M${d.children[0].x},${d.children[0].y}`;
    // Monte jusqu'au parent
    path += ` L${d.children[0].x},${d.y}`;
    // Traverser horizontalement tous les enfants
    path += ` L${d.children[d.children.length - 1].x},${d.y}`;
    // Descendre vers chaque enfant
    d.children.forEach(c => {
        path += ` L${c.x},${c.y}`;
    });

    return path;
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
