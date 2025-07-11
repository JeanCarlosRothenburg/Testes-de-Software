describe('Acesso às páginas de ajuda', () => {

  it('Deve acessar a página de ajuda do Amazon Kindle', () => {
    cy.visit('https://www.amazon.com.br/');
    cy.get('[data-csa-c-slot-id=nav-link-accountList]').click();
    cy.get('[id=ap_email_login]').type('jeancarlosrothenburg13@gmail.com');
    cy.get('[id=continue]').click();
    cy.get('[id=ap_password]').type('Jeanrc784946!');
    cy.get('[id=signInSubmit]').click();
    cy.get('[data-csa-c-slot-id=nav-link-accountList]').click();
    cy.get('[data-card-identifier=DigitalServicesAndDeviceSupport]').click();
    cy.get('.devicetitle')
        .contains( 'Aplicativos Kindle')
        .click();
    cy.get('.help-service-content')
        .find('h1')
        .should('have.text', 'Ajuda do aplicativo Kindle');
  });

  it('Deve acessar a página de ajuda da Amazon Alexa', () => {
    cy.visit('https://www.amazon.com.br/');
    cy.get('[data-csa-c-slot-id=nav-link-accountList]').click();
    cy.get('[id=ap_email_login]').type('jeancarlosrothenburg13@gmail.com');
    cy.get('[id=continue]').click();
    cy.get('[id=ap_password]').type(Cypress.env('SENHA_AMAZON'), { log: false });
    cy.get('[id=signInSubmit]').click();
    cy.get('[data-csa-c-slot-id=nav-link-accountList]').click();
    cy.get('[data-card-identifier=DigitalServicesAndDeviceSupport]').click();
    cy.get('.devicetitle')
        .contains( 'Recursos Alexa')
        .click();
    cy.get('.help-service-content')
        .find('span')
        .should('have.text', 'Ajuda para recursos Alexa');
  });

});