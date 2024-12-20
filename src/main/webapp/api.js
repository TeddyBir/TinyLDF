// Components
const QueryForm = {
    view: (vnode) => {
      const {
        subject,
        predicate,
        object,
        graph,
        onSubjectChange,
        onPredicateChange,
        onObjectChange,
        onGraphChange,
        onSubmit,
        onAddQuadClick, // New prop to handle Add Quad button click
      } = vnode.attrs;
  
      return m('form.mb-4', {
        onsubmit: (event) => {
          event.preventDefault();
          onSubmit();
        }
      }, [
        m('h2', [
          m('a', { href: '/ldf' }, 'LDF Server')
        ]),
        m('fieldset', [
          m('legend', 'Query for QUAD pattern'),
          m('ul', [
            m('li', [
              m('label', { for: 'subject' }, 'subject'),
              m('input.uri', {
                id: 'subject',
                name: 'subject',
                value: subject,
                oninput: (e) => onSubjectChange(e.target.value)
              })
            ]),
            m('li', [
              m('label', { for: 'predicate' }, 'predicate'),
              m('input.uri', {
                id: 'predicate',
                name: 'predicate',
                value: predicate,
                oninput: (e) => onPredicateChange(e.target.value)
              })
            ]),
            m('li', [
              m('label', { for: 'object' }, 'object'),
              m('input.uri', {
                id: 'object',
                name: 'object',
                value: object,
                oninput: (e) => onObjectChange(e.target.value)
              })
            ]),
            m('li', [
              m('label', { for: 'graph' }, 'graph'),
              m('input.uri', {
                id: 'graph',
                name: 'graph',
                value: graph,
                oninput: (e) => onGraphChange(e.target.value)
              })
            ])
          ])
        ]),
        m('p', [
          m('input', { type: 'submit', value: 'Find matching triples' }),
          m('button.btn.btn-secondary.ml-2', {
            type: 'button',
            id: 'addQuadButton',
            onclick: onAddQuadClick // Call the onAddQuadClick prop
          }, 'Add Quad')
        ])
      ]);
    }
  };
  
  const ResultsTable = {
    view: (vnode) => {
      const { results } = vnode.attrs;
      return m('table.table.table-striped', [
        m('thead', [
          m('tr', [
            m('th', 'Subject'),
            m('th', 'Predicate'),
            m('th', 'Object'),
            m('th', 'Graph'),
          ]),
        ]),
        m('tbody', [
          results.map((quad, index) =>
            m('tr', { key: index }, [
              m('td', quad.subject),
              m('td', quad.predicate),
              m('td', quad.object),
              m('td', quad.graph),
            ])
          ),
        ]),
      ]);
    },
  };
  
  const InsertQuadForm = {
    oninit: (vnode) => {
      vnode.state.newSubject = '';
      vnode.state.newPredicate = '';
      vnode.state.newObject = '';
      vnode.state.newGraph = '';
    },
    view: (vnode) => {
      const { onInsert } = vnode.attrs;
      const { newSubject, newPredicate, newObject, newGraph } = vnode.state;
  
      return m('form', [
        m('h3', 'Add New Quad'),
        m('.form-group', [
          m('label', { for: 'newSubject' }, 'Subject:'),
          m('input.form-control', {
            type: 'text',
            id: 'newSubject',
            value: newSubject,
            oninput: (e) => vnode.state.newSubject = e.target.value
          }),
        ]),
        m('.form-group', [
          m('label', { for: 'newPredicate' }, 'Predicate:'),
          m('input.form-control', {
            type: 'text',
            id: 'newPredicate',
            value: newPredicate,
            oninput: (e) => vnode.state.newPredicate = e.target.value
          }),
        ]),
        m('.form-group', [
          m('label', { for: 'newObject' }, 'Object:'),
          m('input.form-control', {
            type: 'text',
            id: 'newObject',
            value: newObject,
            oninput: (e) => vnode.state.newObject = e.target.value
          }),
        ]),
        m('.form-group', [
          m('label', { for: 'newGraph' }, 'Graph:'),
          m('input.form-control', {
            type: 'text',
            id: 'newGraph',
            value: newGraph,
            oninput: (e) => vnode.state.newGraph = e.target.value
          }),
        ]),
        m('button.btn.btn-primary', {
          type: 'button',
          onclick: async () => {
            const newQuad = {
              subject: newSubject,
              predicate: newPredicate,
              object: newObject,
              graph: newGraph
            };
            // Call the onInsert prop (passed from index.js) to handle the insertion
            if (onInsert) {
              await onInsert(newQuad);
              vnode.state.newSubject = '';
              vnode.state.newPredicate = '';
              vnode.state.newObject = '';
              vnode.state.newGraph = '';
            }
          }
        }, 'Add Quad')
      ]);
    }
  };
  
  // Services
  const api = {
    queryLDF: async function({ subject, predicate, object, graph, page }) {
        const queryParams = new URLSearchParams();
        if (subject) queryParams.append('subject', encodeURIComponent(subject));
        if (predicate) queryParams.append('predicate', encodeURIComponent(predicate));
        if (object) queryParams.append('object', encodeURIComponent(object));
        if (graph) queryParams.append('graph', encodeURIComponent(graph));
        if (page) queryParams.append('page', page);
      
        const response = await m.request({
          method: 'GET',
          url: `/ldf?${queryParams}`,
        });
      
        if (response.error) {
          throw new Error(response.error.message || 'Failed to fetch data');
        }
      
        return response;
      },
      insertQuad: async function (quad) {
        console.log('Simulating insertQuad with:', quad);
      
        // Simuler un délai de réponse
        await new Promise(resolve => setTimeout(resolve, 500));
      
        // Simuler une réponse d'erreur (50% de chance)
        // if (Math.random() < 0.5) {
        //   throw new Error('Simulated error during quad insertion');
        // }
      
        // Simuler une réponse de succès
        return {
          message: 'Quad inserted successfully (simulated)',
          insertedQuad: quad,
        };
      }
  }
  
  // Main App
  const App = {
    oninit: () => {
      App.results = [];
      App.metadata = {};
      App.loading = false;
      App.error = null;
      App.executionTime = 0;
      App.subject = '';
      App.predicate = '';
      App.object = '';
      App.graph = '';
      App.page = 1;
      App.showInsertQuadForm = false;
    },
    view: () => {
      return m('div.container.mt-5', [
        m('h1.text-center.mb-4', 'LDF Client'),
        m(QueryForm, {
          subject: App.subject,
          predicate: App.predicate,
          object: App.object,
          graph: App.graph,
          onSubjectChange: (value) => (App.subject = value),
          onPredicateChange: (value) => (App.predicate = value),
          onObjectChange: (value) => (App.object = value),
          onGraphChange: (value) => (App.graph = value),
          onSubmit: () => {
            App.page = 1;
            App.fetchData(1);
          },
          onAddQuadClick: () => App.showInsertQuadForm = !App.showInsertQuadForm
        }),
        m('button.btn.btn-success.mt-3', {
            onclick: () => App.showInsertQuadForm = !App.showInsertQuadForm
          },
          App.showInsertQuadForm ? 'Hide Add Quad Form' : 'Show Add Quad Form'
        ),
        App.showInsertQuadForm && m(InsertQuadForm, {
          onInsert: async (newQuad) => {
            try {
              const response = await api.insertQuad(newQuad);
              console.log('Quad inserted successfully (simulated):', response);
              // Optionally, refresh the results or display a success message here
              App.showInsertQuadForm = false; // Hide the form after successful insertion
            } catch (error) {
              console.error('Error inserting quad:', error);
              // Display an error message to the user
            }
          }
        }),
        App.loading && m('p.text-center', 'Loading...'),
        App.error && m('p.text-center.text-danger', `Error: ${App.error}`),
        App.results.length > 0 && [
          m('p.text-center', `Execution Time: ${App.executionTime} ms`),
          m(ResultsTable, { results: App.results }),
          m('div.d-flex.justify-content-center', [
            m(
              'button.btn.btn-secondary.mr-2',
              {
                onclick: () => App.handlePageChange(App.metadata.previous),
                disabled: !App.metadata.previous,
              },
              'Previous'
            ),
            m(
              'button.btn.btn-secondary',
              {
                onclick: () => App.handlePageChange(App.metadata.next),
                disabled: !App.metadata.next,
              },
              'Next'
            ),
          ]),
        ],
      ]);
    },
    fetchData: async (currentPage) => {
      App.loading = true;
      App.error = null;
      m.redraw();
  
      try {
        const data = await api.queryLDF({
          subject: App.subject,
          predicate: App.predicate,
          object: App.object,
          graph: App.graph,
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
  
  m.mount(document.getElementById('app'), App);