// index.js
import m from 'mithril';
import ResultsTable from './components/ResultsTable';
import InsertQuadForm from './components/InsertQuadForm';
import { queryLDF, insertQuad } from './services/api';

const App = {
    oninit: () => {
        App.results = [];
        App.metadata = {};
        App.loading = false;
        App.error = null;
        App.executionTime = 0;
        App.page = 1;
        App.showInsertQuadForm = false;
    },
    view: () => {
        return m('div', [
            App.showInsertQuadForm && m(InsertQuadForm, {
                onInsert: async (newQuad) => {
                    try {
                        const response = await insertQuad(newQuad);
                        console.log('Quad inserted successfully (simulated):', response);
                        App.showInsertQuadForm = false; 
                    } catch (error) {
                        console.error('Error inserting quad:', error);
                        
                    }
                }
            }),

            App.loading && m('p.text-center', 'Loading...'),
            App.error && m('p.text-center.text-danger', `Error: ${App.error}`),

            App.results.length > 0 && [
                m('h3', `Matches in Wikidata for ${App.subject} ${App.predicate} ${App.object}`),
                m('div.counts', [
                    `Showing triples ${(App.page - 1) * 100 + 1} to ${Math.min(App.page * 100, App.metadata.totalResults)} of ± ${App.metadata.totalResults}`,
                    m('span', { style: { display: 'none' } }, JSON.stringify(App.metadata)),
                    m('ul.links', [
                        App.metadata.previous && m('li', [
                            m('a', {
                                href: '#',
                                onclick: () => App.handlePageChange(App.metadata.previous)
                            }, 'previous')
                        ]),
                        App.metadata.next && m('li', [
                            m('a', {
                                href: '#',
                                onclick: () => App.handlePageChange(App.metadata.next)
                            }, 'next')
                        ])
                    ])
                ]),
                m('p.text-center', `Execution Time: ${App.executionTime} ms`),
                m(ResultsTable, { results: App.results }),
            ],
        ]);
    },
    fetchData: async (currentPage) => {
        App.loading = true;
        App.error = null;
        m.redraw();

        // Récupérer les valeurs du formulaire HTML
        const subject = document.getElementById('subject').value;
        const predicate = document.getElementById('predicate').value;
        const object = document.getElementById('object').value;
        const graph = document.getElementById('graph').value;

        try {
            const data = await queryLDF({
                subject: subject,
                predicate: predicate,
                object: object,
                graph: graph,
                page: currentPage,
            });
            App.results = data.results;
            App.metadata = data.metadata;
            App.executionTime = data.executionTime;
            App.page = data.page;
        } catch (err) {
            App.error = err.message;
        } finally {
            App.loading = false;
            m.redraw();
        }
    },
    handlePageChange: (newPage) => {
        App.fetchData(newPage);
    },
};

document.addEventListener('DOMContentLoaded', () => {
    const queryForm = document.getElementById('queryForm');
    queryForm.addEventListener('submit', (event) => {
        event.preventDefault();
        App.page = 1;
        App.fetchData(1);
    });

    const addQuadButton = document.getElementById('addQuadButton');
    addQuadButton.addEventListener('click', () => {
        App.showInsertQuadForm = !App.showInsertQuadForm;
        m.redraw(); // Forcer le rafraîchissement de l'affichage
    });

    m.mount(document.getElementById('app'), App);
});