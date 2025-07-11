describe('Gestão de mensagens', () => {

  it('Deve consultar as mensagens recebidas', () => {
    cy.visit('https://www.amazon.com.br/');
    cy.get('[data-csa-c-slot-id=nav-link-accountList]').click();
    cy.get('[id=ap_email_login]').type('jeancarlosrothenburg13@gmail.com');
    cy.get('[id=continue]').click();
    cy.get('[id=ap_password]').type(Cypress.env('SENHA_AMAZON'));
    cy.get('[id=signInSubmit]').click();
    cy.get('[data-csa-c-slot-id=nav-link-accountList]').click();
    cy.get('[data-card-identifier=YourMessages]').click();
    cy.get('[id=inbox_all_tab_content]')
        .should('exist')
        .find('tr')
        .its('length')
        .should('be.greaterThan', 0);
  });

  it('Deve visualizar uma mensagem recebida', () => {
    cy.visit('https://www.amazon.com.br/');
    cy.get('[data-csa-c-slot-id=nav-link-accountList]').click();
    cy.get('[id=ap_email_login]').type('jeancarlosrothenburg13@gmail.com');
    cy.get('[id=continue]').click();
    cy.get('[id=ap_password]').type(Cypress.env('SENHA_AMAZON'), { log: false });
    cy.get('[id=signInSubmit]').click();
    cy.get('[data-csa-c-slot-id=nav-link-accountList]').click();
    cy.get('[data-card-identifier=YourMessages]').click();
    cy.contains('.message-table-row-content', 'Enviado: "Gloss Fran By Franciny..."')
        .click();
    cy.get('[id=message-content-title]')
        .find('h3')
        .should('have.text', 'Enviado: "Gloss Fran By Franciny..."');
  });

});