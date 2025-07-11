import { generate } from '@fnando/cpf';

describe('Gerenciamento da conta', () => {
  it('Deve permitir a alteração de CPF utilizando um CPF válido', () => {
    cy.visit('https://www.amazon.com.br/');
    cy.get('[data-csa-c-slot-id=nav-link-accountList]').click();
    cy.get('[id=ap_email_login]').type('jeancarlosrothenburg13@gmail.com');
    cy.get('[id=continue]').click();
    cy.get('[id=ap_password]').type(Cypress.env('SENHA_AMAZON'), { log: false });
    cy.get('[id=signInSubmit]').click();
    cy.get('[data-csa-c-slot-id=nav-link-accountList]').click();
    cy.get('[data-card-identifier=SignInAndSecurity_T2]').click();
    cy.get('[id=CHANGE_CPF_BUTTON]').click();
    cy.get('[id=auth-customer-cpf]').type(generate());
    cy.get('[id=auth-change-cpf-submit]').click();
    cy.get('[id=SUCCESS_MESSAGES]')
        .find('[class=a-alert-content]')
        .should('have.text', 'CPF atualizado');
  })
})