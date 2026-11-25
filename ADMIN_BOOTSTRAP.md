# Administrateur initial

L'initialisation a lieu au demarrage de l'application, pas lors de la compilation.
Renseignez `ADMIN_BOOTSTRAP_EMAIL` et `ADMIN_BOOTSTRAP_PASSWORD` dans les variables
d'environnement ou dans votre fichier `.env` local. Le mot de passe temporaire doit
etre unique, aleatoire, comporter au moins 12 caracteres et au plus 72 octets UTF-8.
En production, fournissez-le via un gestionnaire de secrets ; ne le commitez pas.

Au demarrage, si aucun compte ne porte cet email, un compte `ADMIN` actif est cree
avec un mot de passe hache et le changement de mot de passe obligatoire. Si le compte
existe deja, il n'est pas modifie. Si cet email appartient a un utilisateur non
administrateur, le demarrage echoue : aucun compte n'est promu automatiquement.
Si les deux variables sont absentes, l'initialisation est ignoree avec un avertissement.

1. Connectez-vous avec `POST /auth/login` et le mot de passe temporaire.
2. Avec le JWT retourne, consultez `GET /api/users/me` ; `mustChangePassword` vaut `true`.
3. Envoyez `PUT /api/users/change-password` avec le JWT et un corps JSON contenant
   `oldPassword` et `newPassword`. Le nouveau mot de passe doit respecter les regles
   de validation de l'API.
4. Reconnectez-vous avec le nouveau mot de passe : l'ancien JWT n'est plus valide.

Apres creation, supprimez le mot de passe temporaire de la configuration. L'email
peut rester configure : le compte existant ne sera pas reinitialise. Si ce compte
est supprime ou si la base est recreee, fournissez un nouveau mot de passe temporaire
avant de redemarrer ; sans lui, le demarrage echouera. Dans un deploiement a plusieurs
instances, initialisez le compte avec une seule instance avant de demarrer les autres.
