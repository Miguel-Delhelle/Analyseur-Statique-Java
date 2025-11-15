// dendrogram-clean.ts
// Renderer propre basé sur les vraies données DendroNode

export interface DendroNode {
  leftChild: DendroNode | null;
  rightChild: DendroNode | null;
  hauteurCoupling: number;
  className: string | null;
  leaf: boolean;
}

export class CleanDendrogramRenderer {
  private leaves: string[] = [];
  private leafPositions: Map<string, number> = new Map();
  private nextLeafIndex = 0;

  render(root: DendroNode, container: HTMLElement): void {
    console.log('=== DEBUT RENDU DENDROGRAMME ===');
    console.log('Root node:', root);
    
    // 1. Collecter toutes les feuilles dans l'ordre
    this.leaves = [];
    this.collectLeavesInOrder(root);
    console.log('Feuilles collectées:', this.leaves);
    this.leafPositions.clear();
    this.nextLeafIndex = 0;
    
    // 2. Créer le SVG avec espacement minimal (uniquement pour éviter les chevauchements)
    const leafSpacing = 25;
    const height = 700;
    const margin = { top: 80, right: 80, bottom: 120, left: 120 };
    const estimatedWidth = Math.max(1000, this.leaves.length * leafSpacing + margin.left + margin.right);
    
    const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    svg.setAttribute('width', estimatedWidth.toString());
    svg.setAttribute('height', height.toString());
    svg.style.background = '#ffffff';
    svg.style.border = '1px solid #ccc';
    
    // 3. Dessiner l'arbre en assignant les positions X dynamiquement
    const drawableHeight = height - margin.top - margin.bottom;
    const maxHeight = this.findMaxHeight(root);
    this.drawCompactTree(root, svg, margin, drawableHeight, maxHeight, leafSpacing);
    const finalWidth = Math.max(estimatedWidth, margin.left + (this.nextLeafIndex - 1) * leafSpacing + margin.right);
    svg.setAttribute('width', finalWidth.toString());
    
    // 5. Dessiner l'axe Y
    this.drawYAxis(svg, margin, drawableHeight, maxHeight);
    
    // 6. Dessiner les labels des feuilles
    this.leaves.forEach(leaf => {
      const x = this.leafPositions.get(leaf)!;
      const y = height - margin.bottom + 20;
      
      const text = document.createElementNS('http://www.w3.org/2000/svg', 'text');
      text.setAttribute('x', x.toString());
      text.setAttribute('y', y.toString());
      text.setAttribute('text-anchor', 'start');
      text.setAttribute('font-size', '10px');
      text.setAttribute('font-family', 'Arial, sans-serif');
      text.setAttribute('fill', '#333');
      text.setAttribute('transform', `rotate(-45, ${x}, ${y})`);
      text.textContent = this.getShortName(leaf);
      svg.appendChild(text);
    });
    
    // 6. Créer un conteneur avec scroll et contrôles
    container.innerHTML = '';
    
    // Conteneur avec contrôles
    const controlsDiv = document.createElement('div');
    controlsDiv.style.padding = '10px';
    controlsDiv.style.backgroundColor = '#f8f9fa';
    controlsDiv.style.borderBottom = '1px solid #dee2e6';
    
    const info = document.createElement('span');
    info.textContent = `Dendrogramme: ${this.leaves.length} classes | `;
    info.style.marginRight = '10px';
    
    const fitBtn = document.createElement('button');
    fitBtn.textContent = 'Ajuster à la fenêtre';
    fitBtn.style.marginRight = '10px';
    fitBtn.style.padding = '5px 10px';
    fitBtn.style.backgroundColor = '#007bff';
    fitBtn.style.color = 'white';
    fitBtn.style.border = 'none';
    fitBtn.style.borderRadius = '3px';
    fitBtn.style.cursor = 'pointer';
    
    const resetBtn = document.createElement('button');
    resetBtn.textContent = 'Taille réelle';
    resetBtn.style.marginRight = '10px';
    resetBtn.style.padding = '5px 10px';
    resetBtn.style.backgroundColor = '#28a745';
    resetBtn.style.color = 'white';
    resetBtn.style.border = 'none';
    resetBtn.style.borderRadius = '3px';
    resetBtn.style.cursor = 'pointer';
    
    const scrollDiv = document.createElement('div');
    scrollDiv.style.overflowX = 'auto';
    scrollDiv.style.overflowY = 'auto';
    scrollDiv.style.maxHeight = '600px';
    scrollDiv.style.border = '1px solid #dee2e6';
    scrollDiv.style.backgroundColor = '#ffffff';
    
    // Event pour ajuster à la fenêtre
    fitBtn.addEventListener('click', () => {
      const containerWidth = container.clientWidth - 40; // Marge
      const containerHeight = 500; // Hauteur max
      const scaleX = containerWidth / finalWidth;
      const scaleY = containerHeight / height;
      const scale = Math.min(scaleX, scaleY, 1); // Ne pas agrandir
      
      svg.style.transform = `scale(${scale})`;
      svg.style.transformOrigin = 'top left';
      scrollDiv.style.width = `${finalWidth * scale}px`;
      scrollDiv.style.height = `${height * scale}px`;
      scrollDiv.scrollLeft = 0; // Remettre au début
      
      console.log(`Ajustement: scale=${scale}, containerWidth=${containerWidth}, svgWidth=${finalWidth}`);
    });
    
    // Event pour remettre la taille réelle
    resetBtn.addEventListener('click', () => {
      svg.style.transform = 'scale(1)';
      scrollDiv.style.width = 'auto';
      scrollDiv.style.height = 'auto';
      scrollDiv.scrollLeft = 0;
      console.log('Remis à la taille réelle');
    });
    
    controlsDiv.appendChild(info);
    controlsDiv.appendChild(fitBtn);
    controlsDiv.appendChild(resetBtn);
    scrollDiv.appendChild(svg);
    
    container.appendChild(controlsDiv);
    container.appendChild(scrollDiv);
    
    console.log('=== FIN RENDU DENDROGRAMME ===');
  }
  
  private collectLeavesInOrder(node: DendroNode): void {
    if (node.leaf && node.className) {
      // Seules les vraies feuilles (classes) sont comptées
      this.leaves.push(node.className);
      console.log(`Feuille trouvée: ${node.className}`);
      return;
    }
    
    // Nœud interne - parcourir les enfants
    console.log(`Nœud interne: hauteur=${node.hauteurCoupling}, enfants: ${node.leftChild ? 'gauche' : 'pas de gauche'}, ${node.rightChild ? 'droite' : 'pas de droite'}`);
    
    // Parcours in-order : left -> right
    if (node.leftChild) {
      this.collectLeavesInOrder(node.leftChild);
    }
    if (node.rightChild) {
      this.collectLeavesInOrder(node.rightChild);
    }
  }
  
  private drawCompactTree(
    node: DendroNode,
    svg: SVGElement,
    margin: { top: number; right: number; bottom: number; left: number },
    drawableHeight: number,
    maxHeight: number,
    leafSpacing: number
  ): { x: number; y: number } {
    if (node.leaf && node.className) {
      const x = margin.left + this.nextLeafIndex * leafSpacing;
      const y = margin.top + drawableHeight;
      this.leafPositions.set(node.className, x);
      this.nextLeafIndex += 1;
      return { x, y };
    }

    const leftPos = node.leftChild
      ? this.drawCompactTree(node.leftChild, svg, margin, drawableHeight, maxHeight, leafSpacing)
      : null;
    const rightPos = node.rightChild
      ? this.drawCompactTree(node.rightChild, svg, margin, drawableHeight, maxHeight, leafSpacing)
      : null;

    if (!leftPos || !rightPos) {
      return leftPos || rightPos || { x: margin.left, y: margin.top + drawableHeight };
    }

    const normalizedHeight = maxHeight === 0 ? 0 : node.hauteurCoupling / maxHeight;
    const y = margin.top + (1.0 - normalizedHeight) * drawableHeight;
    const x = (leftPos.x + rightPos.x) / 2;

    this.drawLine(svg, leftPos.x, y, rightPos.x, y, '#333', 1);
    this.drawLine(svg, leftPos.x, y, leftPos.x, leftPos.y, '#333', 1);
    this.drawLine(svg, rightPos.x, y, rightPos.x, rightPos.y, '#333', 1);

    return { x, y };
  }
  
  private drawLine(svg: SVGElement, x1: number, y1: number, x2: number, y2: number, color: string, width: number): void {
    const line = document.createElementNS('http://www.w3.org/2000/svg', 'line');
    line.setAttribute('x1', x1.toString());
    line.setAttribute('y1', y1.toString());
    line.setAttribute('x2', x2.toString());
    line.setAttribute('y2', y2.toString());
    line.setAttribute('stroke', color);
    line.setAttribute('stroke-width', width.toString());
    svg.appendChild(line);
  }
  

  private drawYAxis(svg: SVGElement, margin: any, drawableHeight: number, maxHeight: number): void {
    // Ligne de l'axe Y
    this.drawLine(svg, margin.left - 15, margin.top, margin.left - 15, margin.top + drawableHeight, '#333', 2);
    
    // Graduations basées sur les vraies valeurs
    const numTicks = 6;
    for (let i = 0; i <= numTicks; i++) {
      const ratio = i / numTicks;
      const realValue = ratio * maxHeight; // De 0.0 à maxHeight
      const y = margin.top + (1.0 - ratio) * drawableHeight; // Inverser pour affichage
      
      // Graduation
      const tickLength = (i % 2 === 0) ? 12 : 6;
      this.drawLine(svg, margin.left - 15 - tickLength, y, margin.left - 15, y, '#333', 1);
      
      // Label pour les graduations principales
      if (i % 2 === 0) {
        const text = document.createElementNS('http://www.w3.org/2000/svg', 'text');
        text.setAttribute('x', (margin.left - 25).toString());
        text.setAttribute('y', (y + 4).toString());
        text.setAttribute('text-anchor', 'end');
        text.setAttribute('font-size', '11px');
        text.setAttribute('font-family', 'Arial, sans-serif');
        text.setAttribute('fill', '#333');
        text.setAttribute('font-weight', 'bold');
        text.textContent = realValue.toFixed(3);
        svg.appendChild(text);
      }
    }
    
    // Titre de l'axe Y
    const title = document.createElementNS('http://www.w3.org/2000/svg', 'text');
    title.setAttribute('x', '20');
    title.setAttribute('y', (margin.top + drawableHeight / 2).toString());
    title.setAttribute('text-anchor', 'middle');
    title.setAttribute('font-size', '12px');
    title.setAttribute('font-family', 'Arial, sans-serif');
    title.setAttribute('font-weight', 'bold');
    title.setAttribute('fill', '#333');
    title.setAttribute('transform', `rotate(-90, 20, ${margin.top + drawableHeight / 2})`);
    title.textContent = 'Hauteur de Couplage';
    svg.appendChild(title);
  }

  private findMaxHeight(node: DendroNode): number {
    if (node.leaf) {
      return node.hauteurCoupling; // Feuilles = 0.0
    }
    
    let maxHeight = node.hauteurCoupling;
    
    if (node.leftChild) {
      maxHeight = Math.max(maxHeight, this.findMaxHeight(node.leftChild));
    }
    if (node.rightChild) {
      maxHeight = Math.max(maxHeight, this.findMaxHeight(node.rightChild));
    }
    
    return maxHeight;
  }

  private getShortName(className: string): string {
    const parts = className.split('.');
    return parts[parts.length - 1] || className;
  }
}
