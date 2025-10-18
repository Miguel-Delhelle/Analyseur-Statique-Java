import { Graphviz } from "@hpcc-js/wasm";

// Définition des types pour la réponse de notre API (pour la sécurité de typage)
interface metricsDto {
    [key: string]: any; // Permet d'accéder aux clés dynamiquement
}
interface graphDotDTO {
    dotContent: string;
}
interface AnalysisResponse {
    metricsDto: metricsDto;
    graphDotDTO: graphDotDTO;
}

const graphviz = await Graphviz.load();

// Références aux éléments du DOM
const gitUrlInput = document.getElementById('gitUrlInput') as HTMLInputElement;
const analyzeGitBtn = document.getElementById('analyzeGitBtn') as HTMLButtonElement;
const zipFileInput = document.getElementById('zipFileInput') as HTMLInputElement;
const analyzeZipBtn = document.getElementById('analyzeZipBtn') as HTMLButtonElement;

const resultsSection = document.getElementById('results') as HTMLElement;
const loader = document.getElementById('loader') as HTMLElement;
const resultsContent = document.getElementById('results-content') as HTMLElement;

const metricsTableBody = document.getElementById('metricsTableBody') as HTMLElement;
const graphContainer = document.getElementById('graphContainer') as HTMLElement;
const downloadBtn = document.getElementById('downloadBtn') as HTMLAnchorElement;



// --- GESTIONNAIRES D'ÉVÉNEMENTS ---

analyzeGitBtn.addEventListener('click', () => {
    console.log(gitUrlInput.value);
    const uriGit = gitUrlInput.value;
    if (!uriGit) {
        alert("Veuillez fournir une URL de dépôt Git.");
        return;
    }
    // Crée le corps de la requête
    const body = JSON.stringify({ uriGit: uriGit

     });
    // Appelle la fonction d'analyse
    fetchAnalysisResults('/api/analyses/git', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: body
    });
});

analyzeZipBtn.addEventListener('click', () => {
    const file = zipFileInput.files?.[0];
    if (!file) {
        alert("Veuillez sélectionner un fichier .zip.");
        return;
    }
    // Crée le corps de la requête avec FormData
    const formData = new FormData();
    formData.append('file', file);
    // Appelle la fonction d'analyse
    fetchAnalysisResults('/api/analyses/zip', {
        method: 'POST',
        body: formData
    });
});


// --- FONCTIONS LOGIQUES ---

/**
 * Fonction centrale pour appeler l'API et gérer l'affichage.
 */
async function fetchAnalysisResults(url: string, options: RequestInit) {
    // Affiche le loader et cache les anciens résultats
    resultsSection.classList.remove('hidden');
    resultsContent.classList.add('hidden');
    loader.classList.remove('hidden');

    try {
        const response = await fetch(url, options);

        if (!response.ok) {
            throw new Error(`Erreur du serveur : ${response.status} ${response.statusText}`);
        }

        const data: AnalysisResponse = await response.json();
        
        // Affiche les résultats
        displayMetrics(data.metricsDto);
        await displayGraph(data.graphDotDTO);

        resultsContent.classList.remove('hidden');

    } catch (error) {
        console.error("Erreur lors de l'analyse:", error);
        alert("Une erreur est survenue. Vérifiez la console pour plus de détails.");
        resultsSection.classList.add('hidden'); // Cache la section des résultats en cas d'erreur
    } finally {
        // Cache le loader à la fin, que ça ait réussi ou non
        loader.classList.add('hidden');
    }
}

/**
 * Affiche les métriques dans le tableau HTML.
 */
function displayMetrics(metrics: metricsDto) {
    // Vide le tableau précédent
    metricsTableBody.innerHTML = '';
    
    // Itère sur les clés de l'objet de métriques et crée les lignes du tableau
    for (const [key, value] of Object.entries(metrics)) {
        const row = document.createElement('tr');
        
        const keyCell = document.createElement('td');
        keyCell.textContent = key; // Le nom de la métrique
        
        const valueCell = document.createElement('td');
        // Formate les nombres pour une meilleure lisibilité
        valueCell.textContent = typeof value === 'number' ? value.toFixed(2) : String(value);

        row.appendChild(keyCell);
        row.appendChild(valueCell);
        metricsTableBody.appendChild(row);
    }
}

/**
 * Génère et affiche le graphe SVG à partir de la chaîne DOT.
 */
async function displayGraph(graphDot: graphDotDTO) {
    graphContainer.innerHTML = '<p>Génération du graphe...</p>';
    downloadBtn.classList.add('hidden'); // Cache le bouton au début

    if (!graphDot || !graphDot.dotContent) {
        // ... (gestion d'erreur)
        return;
    }

    try {
        const graphviz = await Graphviz.load();
        const svgString = graphviz.layout(graphDot.dotContent, "svg", "dot");
        
        // Affiche le graphe
        graphContainer.innerHTML = svgString;


        // 1. Crée un objet "Blob" à partir de la chaîne SVG.
        // Un Blob représente des données brutes (comme un fichier).
        const blob = new Blob([svgString], { type: 'image/svg+xml' });

        // 2. Crée une URL temporaire qui pointe vers ce Blob en mémoire.
        const url = URL.createObjectURL(blob);

        // 3. Configure le bouton de téléchargement.
        downloadBtn.href = url;
        // On peut même donner un nom de fichier dynamique
        downloadBtn.download = `call-graph-${new Date().toISOString()}.svg`;
        
        // 4. Affiche le bouton.
        downloadBtn.classList.remove('hidden');

    } catch (error) {
        console.error("Erreur lors de la génération du graphe DOT:", error);
        graphContainer.innerHTML = '<p style="color: red;">Erreur lors de la génération du graphe.</p>';
    }
}