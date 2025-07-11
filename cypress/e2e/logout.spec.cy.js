describe('template spec', () => {
  it('passes', () => {
    cy.visit('https://www.amazon.com.br/');
    cy.get('[data-csa-c-slot-id=nav-link-accountList]').click();
    cy.get('[id=ap_email_login]').type('jeancarlosrothenburg13@gmail.com');
    cy.get('[id=continue]').click();
    cy.get('[id=ap_password]').type(Cypress.env('SENHA_AMAZON'), { log: false });
    cy.get('[id=signInSubmit]').click();
    cy.get('[data-csa-c-slot-id=nav-link-accountList]').click();
    cy.get('[data-card-identifier=SignInAndSecurity_T2]').click();
    cy.get('[id=SECURE_YOUR_ACCOUNT_BUTTON]').click();
    cy.get('[id=sign_out_everything_button-announce]').click();
    cy.get('[id=sign_out_everything_continue_button]').click();
    cy.get('[id=chimera_sya_sign_out_all_sessions_success_confirmed]')
        .find('span')
        .should('have.text', 'Saída confirmada');
  })
})