package com.mmk.kmpauth.google

/**
 * Provider class for Google Authentication UI part. a.k.a [signIn]
 */
public interface GoogleAuthUiProvider {

    public companion object {
        internal val BASIC_AUTH_SCOPE = listOf("email", "profile")
    }

    /**
     * Opens Sign In with Google UI, and returns [GoogleUser]
     * if sign-in was successful, otherwise, null
     * By default all available accounts are listed to choose from
     * @see signIn(filterByAuthorizedAccounts: Boolean)
     * @return returns GoogleUser or null(if sign-in was not successful)
     */
    public suspend fun signIn(): GoogleUser? =
        signIn(filterByAuthorizedAccounts = false, scopes = BASIC_AUTH_SCOPE)

    /**
     * @param filterByAuthorizedAccounts set to true so users can choose between available accounts to sign in.
     * setting to false list any accounts that have previously been used to sign in to your app.
     */
    public suspend fun signIn(filterByAuthorizedAccounts: Boolean): GoogleUser? =
        signIn(filterByAuthorizedAccounts = filterByAuthorizedAccounts, scopes = BASIC_AUTH_SCOPE)


    /**
     * @param filterByAuthorizedAccounts set to true so users can choose between available accounts to sign in.
     * setting to false list any accounts that have previously been used to sign in to your app. Default value is false.
     * @param scopes Custom scopes to retrieve more information. Default value listOf("email", "profile")
     *
     */
    public suspend fun signIn(
        filterByAuthorizedAccounts: Boolean = false,
        scopes: List<String> = BASIC_AUTH_SCOPE
    ): GoogleUser?
}
