// File: src/main.ts
/*
 * Dependencies to install:
 * npm install cytoscape chart.js
 * npm install --save-dev @types/cytoscape
 */

import cytoscape from 'cytoscape';
import { Chart, registerables } from 'chart.js/auto';

Chart.register(...registerables);

// Types pour l'API response
interface MetricsDto {
    [key: string]: number;
}

interface GraphNode {
    id: string;
    label?: string;
}

interface GraphEdge {
    source: string;
    target: string;
    weight?: number;
}

interface GraphDTO {
    adjacencyList: { [key: string]: Array<{calleeSignature: string, type: string}> };
}

interface CouplingEdge {
    classA: string;
    classB: string;
    nbrOfLink: number;
    coupling: number;
}

interface TreeCoupling {
    graphCoupling: CouplingEdge[];
}

interface AnalysisResponse {
    metricsDto: MetricsDto;
    graphDTO: GraphDTO;
    treeCoupling: TreeCoupling;
}

class AnalyzerApp {
    private gitUriInput!: HTMLInputElement;
    private analyzeBtn!: HTMLButtonElement;
    private loader!: HTMLElement;
    private errorDiv!: HTMLElement;
    private results!: HTMLElement;
    private metricsCards!: HTMLElement;
    private metricsSummary!: HTMLElement;
    private callGraph!: HTMLElement;
    private couplingGraph!: HTMLElement;


    constructor() {
        this.initializeElements();
        this.bindEvents();
    }

    private initializeElements(): void {
        this.gitUriInput = document.getElementById('gitUri') as HTMLInputElement;
        this.analyzeBtn = document.getElementById('analyzeBtn') as HTMLButtonElement;
        this.loader = document.getElementById('loader') as HTMLElement;
        this.errorDiv = document.getElementById('error') as HTMLElement;
        this.results = document.getElementById('results') as HTMLElement;
        this.metricsCards = document.getElementById('metrics-cards') as HTMLElement;
        this.metricsSummary = document.getElementById('metrics-summary') as HTMLElement;
        this.callGraph = document.getElementById('call-graph') as HTMLElement;
        this.couplingGraph = document.getElementById('coupling-graph') as HTMLElement;
    }

    private bindEvents(): void {
        this.analyzeBtn.addEventListener('click', () => this.analyzeProject());
        this.gitUriInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.analyzeProject();
        });
    }

    private showLoader(): void {
        this.loader.classList.remove('hidden');
        this.errorDiv.classList.add('hidden');
        this.results.classList.add('hidden');
        this.analyzeBtn.disabled = true;
    }

    private hideLoader(): void {
        this.loader.classList.add('hidden');
        this.analyzeBtn.disabled = false;
    }

    private showError(message: string): void {
        this.errorDiv.textContent = message;
        this.errorDiv.classList.remove('hidden');
    }

    private showResults(): void {
        this.results.classList.remove('hidden');
    }

    private async analyzeProject(): Promise<void> {
        const gitUri = this.gitUriInput.value.trim();
        if (!gitUri) {
            this.showError('Veuillez saisir une URL Git valide');
            return;
        }

        this.showLoader();

        try {
            const response = await fetch('http://localhost:8090/api/analyses/git', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ uriGit: gitUri })
            });

            if (!response.ok) {
                throw new Error(`Erreur HTTP: ${response.status}`);
            }

            const data: AnalysisResponse = await response.json();
            this.displayResults(data);
            this.showResults();
        } catch (error) {
            this.showError(`Erreur lors de l'analyse: ${error instanceof Error ? error.message : 'Erreur inconnue'}`);
        } finally {
            this.hideLoader();
        }
    }

    private displayResults(data: AnalysisResponse): void {
        this.displayMetrics(data.metricsDto);
        this.displayCallGraph(data.graphDTO);
        this.displayCouplingGraph(data.treeCoupling);
    }

    private displayMetrics(metrics: MetricsDto): void {
        // Affichage des cartes métriques
        this.metricsCards.innerHTML = '';
        Object.entries(metrics).forEach(([key, value]) => {
            const card = document.createElement('div');
            card.className = 'metric-card';
            card.innerHTML = `
                <h3>${this.formatMetricName(key)}</h3>
                <div class="metric-value">${value}</div>
            `;
            this.metricsCards.appendChild(card);
        });

        // Résumé textuel
        const totalMetrics = Object.keys(metrics).length;
        const avgValue = Object.values(metrics).reduce((a, b) => a + b, 0) / totalMetrics;
        this.metricsSummary.innerHTML = `
            <div class="summary">
                <p><strong>Résumé:</strong> ${totalMetrics} métriques analysées avec une valeur moyenne de ${avgValue.toFixed(2)}</p>
            </div>
        `;
    }

    private formatMetricName(name: string): string {
        return name.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase());
    }

    private displayCallGraph(graphData: GraphDTO): void {
        this.callGraph.innerHTML = '';
        
        const elements = this.convertAdjacencyListToCytoscape(graphData.adjacencyList);
        
        cytoscape({
            container: this.callGraph,
            elements: elements,
            style: [
                {
                    selector: 'node',
                    style: {
                        'background-color': '#3498db',
                        'label': 'data(label)',
                        'text-valign': 'center',
                        'color': '#fff',
                        'font-size': '12px',
                        'width': '60px',
                        'height': '60px'
                    }
                },
                {
                    selector: 'edge',
                    style: {
                        'width': 2,
                        'line-color': '#95a5a6',
                        'target-arrow-color': '#95a5a6',
                        'target-arrow-shape': 'triangle',
                        'curve-style': 'bezier'
                    }
                }
            ],
            layout: {
                name: 'cose',
                animate: true,
                animationDuration: 1000
            }
        });
    }

    private displayCouplingGraph(couplingData: TreeCoupling): void {
        this.couplingGraph.innerHTML = '';
            
        const elements = this.convertCouplingToCytoscape(couplingData.graphCoupling);
        
        cytoscape({
            container: this.couplingGraph,
            elements: elements,
            style: [
                {
                    selector: 'node',
                    style: {
                        'background-color': '#e74c3c',
                        'label': 'data(label)',
                        'text-valign': 'center',
                        'color': '#fff',
                        'font-size': '12px',
                        'width': (ele: any) => Math.max(40, Math.min(100, (ele.data('coupling') || 1) * 10)),
                        'height': (ele: any) => Math.max(40, Math.min(100, (ele.data('coupling') || 1) * 10))
                    }
                },
                {
                    selector: 'edge',
                    style: {
                        'width': (ele: any) => Math.max(2, Math.min(10, (ele.data('coupling') || 1) * 2)),
                        'line-color': (ele: any) => {
                            const coupling = ele.data('coupling') || 0;
                            const intensity = Math.min(coupling / 10, 1);
                            return `rgb(${Math.round(149 + (231 - 149) * intensity)}, ${Math.round(165 + (76 - 165) * intensity)}, ${Math.round(166 + (60 - 166) * intensity)})`;
                        },
                        'target-arrow-color': (ele: any) => {
                            const coupling = ele.data('coupling') || 0;
                            const intensity = Math.min(coupling / 10, 1);
                            return `rgb(${Math.round(149 + (231 - 149) * intensity)}, ${Math.round(165 + (76 - 165) * intensity)}, ${Math.round(166 + (60 - 166) * intensity)})`;
                        },
                        'target-arrow-shape': 'triangle',
                        'curve-style': 'bezier',
                        'opacity': (ele: any) => Math.max(0.5, Math.min(1, 0.5 + (ele.data('coupling') || 0) / 20))
                    }
                }
            ],
            layout: {
                name: 'cose',
                animate: true,
                animationDuration: 1000
            }
        });
    }

    private convertAdjacencyListToCytoscape(adjacencyList: { [key: string]: any[] }): any[] {
        const elements: any[] = [];
        const nodes = new Set<string>();

        // Collecter tous les nœuds
        Object.keys(adjacencyList).forEach(source => {
            nodes.add(source);
            adjacencyList[source].forEach(edge => {
                if (edge.calleeSignature) {
                    nodes.add(edge.calleeSignature);
                }
            });
        });

        // Ajouter les nœuds avec des labels simplifiés
        nodes.forEach(node => {
            const simplifiedLabel = this.simplifyMethodSignature(node);
            elements.push({
                data: { 
                    id: node, 
                    label: simplifiedLabel,
                    fullSignature: node
                }
            });
        });

        // Ajouter les arêtes
        Object.entries(adjacencyList).forEach(([source, edges]) => {
            edges.forEach(edge => {
                if (edge.calleeSignature) {
                    elements.push({
                        data: { 
                            id: `${source}-${edge.calleeSignature}`,
                            source: source,
                            target: edge.calleeSignature,
                            type: edge.type
                        }
                    });
                }
            });
        });

        return elements;
    }

    private simplifyMethodSignature(signature: string): string {
        // Extraire le nom de la classe et de la méthode
        const parts = signature.split('#');
        if (parts.length === 2) {
            const className = parts[0].split('.').pop() || parts[0];
            const methodPart = parts[1].split('(')[0];
            return `${className}.${methodPart}`;
        }
        // Fallback pour les signatures sans #
        const lastDot = signature.lastIndexOf('.');
        if (lastDot > 0) {
            return signature.substring(lastDot + 1);
        }
        return signature;
    }

    private convertCouplingToCytoscape(couplingEdges: CouplingEdge[]): any[] {
        const elements: any[] = [];
        const nodes = new Set<string>();

        // Collecter tous les nœuds
        couplingEdges.forEach(edge => {
            nodes.add(edge.classA);
            nodes.add(edge.classB);
        });

        // Ajouter les nœuds avec couplage
        nodes.forEach(node => {
            const nodeCoupling = couplingEdges
                .filter(e => e.classA === node || e.classB === node)
                .reduce((sum, e) => sum + e.coupling, 0);
            
            const simplifiedLabel = this.simplifyClassName(node);
            elements.push({
                data: { 
                    id: node, 
                    label: simplifiedLabel,
                    fullName: node,
                    coupling: nodeCoupling
                }
            });
        });

        // Ajouter les arêtes
        couplingEdges.forEach(edge => {
            elements.push({
                data: {
                    id: `${edge.classA}-${edge.classB}`,
                    source: edge.classA,
                    target: edge.classB,
                    coupling: edge.coupling,
                    nbrOfLink: edge.nbrOfLink
                }
            });
        });

        return elements;
    }

    private simplifyClassName(className: string): string {
        // Extraire juste le nom de la classe sans le package complet
        const parts = className.split('.');
        return parts[parts.length - 1] || className;
    }
}

// Initialisation de l'application
document.addEventListener('DOMContentLoaded', () => {
    new AnalyzerApp();
});