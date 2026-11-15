<p align="center">
  <img src="docs/assets/banner.svg" alt="VanillaPlus — Votre aventure survie, enrichie. Paysage de collines et de sapins en blocs." width="100%">
</p>

<p align="center">
  <a href="https://github.com/Skyzenfr/VanillaPlus/releases/latest"><img src="https://img.shields.io/badge/Minecraft-1.21-78be58?style=for-the-badge" alt="Minecraft 1.21"></a>
  <img src="https://img.shields.io/badge/Java-21-e8b968?style=for-the-badge" alt="Java 21">
  <img src="https://img.shields.io/badge/Plugin-1.0-69bfc6?style=for-the-badge" alt="Version du plugin : 1.0">
  <img src="https://img.shields.io/badge/API-Spigot-abb8c8?style=for-the-badge" alt="API Spigot">
</p>

<p align="center">
  Un plugin de survie en français qui rassemble déplacements, économie, échanges et petits conforts du quotidien.<br>
  <strong>Explorez, construisez et jouez ensemble, dans l'esprit de Minecraft.</strong>
</p>

<p align="center">
  <a href="https://github.com/Skyzenfr/VanillaPlus/releases/latest/download/VanillaPlus-1.0-mc1.21.jar"><strong>📦 Télécharger le JAR</strong></a>
  &nbsp;·&nbsp; <a href="#-installation">Installation</a>
  &nbsp;·&nbsp; <a href="#-commandes">Commandes</a>
  &nbsp;·&nbsp; <a href="#-configuration">Configuration</a>
  &nbsp;·&nbsp; <a href="https://github.com/Skyzenfr/VanillaPlus/issues">Signaler un problème</a>
</p>

---

## 🌿 Une survie plus pratique

| 🧭 Se déplacer | 💰 Échanger | ✨ Personnaliser |
| :--- | :--- | :--- |
| Menu de téléportation vers les joueurs connectés, spawn et warps partagés. Compte à rebours et annulation au mouvement. | Monnaie interne, paiements entre joueurs et hôtel des ventes avec interface en jeu. Récompenses pour des activités de survie. | Achat de couleurs et d'icônes de pseudo, chat interactif, messages de connexion et tablist personnalisés. |
| **🪓 Faciliter le quotidien** | **📊 Suivre sa progression** | **💬 Garder le contact** |
| Établi accessible partout, abattage progressif des troncs connectés à la hache et réglage de la perte de nourriture. | Interfaces de statistiques : temps de jeu, morts, victimes, minage, récoltes et autres statistiques Minecraft. | Messages privés avec délai anti-spam et historique des dix derniers messages sauvegardé par joueur. |

> **Repères de version :** `1.21` désigne la version Minecraft ciblée. `1.0` est la version interne du plugin, déclarée dans `plugin.yml` et Maven.

## 📦 Installation

### Prérequis

| Élément | Version / condition |
| :--- | :--- |
| Serveur | **Spigot 1.21**, ou serveur compatible avec son API |
| Java | **21** pour exécuter le plugin |
| Plugins supplémentaires | Aucune dépendance de plugin déclarée ; l'économie est interne, sans Vault |
| Côté joueur | Aucun mod client requis |

La compilation cible l'API Spigot `1.21-R0.1-SNAPSHOT`. Les autres versions de Minecraft ne sont pas garanties ; vérifiez la compatibilité sur un serveur de test avant une mise à jour.

1. Téléchargez **[VanillaPlus-1.0-mc1.21.jar](https://github.com/Skyzenfr/VanillaPlus/releases/latest/download/VanillaPlus-1.0-mc1.21.jar)** depuis la [dernière release](https://github.com/Skyzenfr/VanillaPlus/releases/latest). Les archives *Source code* ne sont pas installables comme plugin.
2. Arrêtez votre serveur et sauvegardez votre monde ainsi que les données des plugins.
3. Placez le JAR dans le dossier `plugins/`. En cas de mise à jour, remplacez l'ancien JAR de VanillaPlus pour ne garder qu'une seule copie.
4. Démarrez le serveur. Le dossier `plugins/VanillaPlus/` et ses fichiers sont créés au premier chargement.
5. Consultez la console, puis utilisez `/help`, `/craft` et `/money` en jeu pour un premier contrôle.
6. Arrêtez le serveur pour adapter `config.yml`, puis redémarrez-le pour appliquer les changements.

```text
serveur/
└── plugins/
    ├── VanillaPlus-1.0-mc1.21.jar
    └── VanillaPlus/
        ├── config.yml
        ├── warps.yml
        ├── market.yml
        └── messages.yml
```

Pour définir le point d'accueil, placez-vous à l'endroit souhaité dans le monde `world`, puis lancez `/spawn create` avec un compte opérateur.

## 🎮 Commandes

Les commandes ci-dessous s'utilisent **en jeu par un joueur**. La colonne « Accès » décrit les contrôles présents dans le code.

### Quotidien et déplacements

| Commande | Action | Alias | Accès |
| :--- | :--- | :--- | :--- |
| `/help` | Affiche l'aide avec des commandes cliquables | `/aide` | Tous |
| `/craft` | Ouvre un établi | — | Tous |
| `/spawn` | Rejoint le spawn du monde `world` | `/hub`, `/lobby` | Tous |
| `/spawn create` | Définit le spawn à la position du joueur | `/hub create`, `/lobby create` | **OP** |
| `/tp` | Ouvre le menu des joueurs connectés | `/tpa`, `/teleport` | Tous |
| `/warp` | Ouvre le menu des warps partagés | `/home` | Tous |
| `/warp create <nom>` | Enregistre un warp à la position actuelle | `/home create <nom>` | Tous |
| `/warp delete <nom>` | Supprime un warp par son nom | `/home delete <nom>` | Tous |
| `/stats` | Ouvre les statistiques et l'accès aux autres joueurs | `/stat`, `/s` | Tous |

`/tp` ouvre une interface : la sélection du joueur se fait dans ce menu. L'alias `/tpa` ne met pas en place de demande à accepter. `/home` est un alias des **warps partagés**, pas un système de maisons privées. La suppression du spawn n'est pas implémentée.

### Économie et marché

| Commande | Action | Alias | Accès |
| :--- | :--- | :--- | :--- |
| `/money` | Affiche son solde | `/balance`, `/bank`, `/argent` | Tous |
| `/pay <joueur> <montant>` | Envoie de l'argent à un joueur connecté | — | Tous |
| `/money add <montant> <joueur>` | Ajoute de l'argent à un joueur connecté | Alias de `/money` | **OP** |
| `/money remove <montant> <joueur>` | Retire de l'argent à un joueur connecté | Alias de `/money` | **OP** |
| `/market` | Ouvre l'hôtel des ventes | — | Tous |
| `/market sell <prix>` | Met en vente la pile entière tenue dans la main principale | — | Tous |
| `/nickname` | Ouvre la boutique de couleurs et d'icônes de pseudo | — | Tous |

**Exemple :** tenez les objets à vendre, lancez `/market sell 150`, puis retrouvez l'annonce avec `/market`. Cliquez sur une annonce pour acheter ; faites **Maj + clic sur votre propre annonce** pour la retirer. Les annonces expirent après sept jours. Les couleurs et icônes de pseudo coûtent chacune `100` unités de monnaie dans le code actuel.

### Messages privés

| Commande | Action | Alias déclarés | Accès |
| :--- | :--- | :--- | :--- |
| `/msg <joueur> <message>` | Écrit à un joueur connecté, avec un délai de cinq secondes | `/mp`, `/message`, `/messages` | Tous |
| `/reply <message>` | Répond au dernier interlocuteur | — | Tous |
| `/msghistory` | Affiche les dix derniers messages privés conservés | — | Tous |

L'historique est enregistré dans `messages.yml`. Consultez les limites ci-dessous concernant `/reply` et les alias de `/msg`.

### Accès et comportements à connaître

- Aucun nœud de permission `vanillaplus.*` n'est déclaré. Les opérations administratives sur le solde et la création du spawn utilisent le statut **OP**.
- La création et la suppression de warps par commande sont ouvertes à tous les joueurs dans l'implémentation actuelle.
- Certaines commandes natives, dont `/plugins`, `/pl`, `/version`, `/seed`, `/tell` et `/w`, sont bloquées par le plugin pour tous les joueurs, y compris les opérateurs.
- Les commandes telles que `/tp`, `/help` ou `/money` peuvent entrer en conflit avec celles d'autres plugins. Utilisez si nécessaire leur forme préfixée, par exemple `/vanillaplus:craft`.

## ⚙️ Configuration

Le fichier de référence est [`config.yml`](VanillaPlus/src/main/resources/config.yml). Modifiez la copie générée dans `plugins/VanillaPlus/` lorsque le serveur est arrêté, puis redémarrez-le. Aucune commande de rechargement n'est fournie.

| Paramètre | Valeur par défaut | Rôle |
| :--- | :--- | :--- |
| `discord` | Lien fourni dans le fichier | Lien communautaire affiché dans les messages |
| `motd.line1` | Texte VanillaPlus | Première ligne de description du serveur |
| `tab_header` / `tab_footer` | Texte VanillaPlus / aide | En-tête et pied de la liste des joueurs |
| `command_restricted` | Message en français | Message de restriction utilisé par les commandes |
| `hunger_decrease_rate` | `0.5` | Multiplicateur de perte de nourriture, avec arrondi supérieur |
| `teleport.delay` | `5` | Attente avant téléportation, en secondes |
| `teleport.cooldown` | `10` | Attente entre les téléportations, en secondes |
| `teleport.cancelOnMove` | `true` | Annule la téléportation si le joueur se déplace |
| `teleport.messages.*` | Messages en français | Textes d'attente, d'annulation, de réussite et de cooldown |
| `teleport.titles.*` | Titres et sous-titres | Affichage du compte à rebours et du résultat |

<details>
<summary><strong>Exemple : adapter les téléportations</strong></summary>

Dans la section `teleport` existante, vous pouvez par exemple modifier les paramètres suivants. Conservez les autres sous-sections du fichier d'origine, notamment `titles`.

```yaml
teleport:
  delay: 3
  cooldown: 15
  cancelOnMove: true
  messages:
    waiting: "&eTéléportation dans {delay} secondes. Ne bougez pas !"
    cancelled: "&cTéléportation annulée car vous avez bougé."
    teleported: "&aVous avez été téléporté."
    cooldown: "&cPatientez encore {timeLeft}{timeUnit}."
```

Les textes de téléportation reconnaissent les couleurs `&`. Les variables disponibles selon le message sont `{delay}`, `{timeLeft}`, `{timeUnit}` et `{countdown}` pour le sous-titre du compte à rebours. Les durées `fadeIn`, `stay` et `fadeOut` des titres sont exprimées en ticks : `20` ticks correspondent à une seconde au rythme normal du serveur.

</details>

### Données à sauvegarder

| Emplacement | Contenu |
| :--- | :--- |
| `plugins/VanillaPlus/config.yml` | Réglages du plugin |
| `plugins/VanillaPlus/warps.yml` | Positions et informations des warps |
| `plugins/VanillaPlus/market.yml` | Annonces de l'hôtel des ventes |
| `plugins/VanillaPlus/messages.yml` | Historiques des messages privés ; ce fichier n'est pas un catalogue de traductions |
| Données joueurs du monde | Monnaie et personnalisation enregistrées via le conteneur de données persistantes Bukkit |

Les montants des récompenses, le prix des cosmétiques, la durée des annonces et la liste des commandes bloquées sont actuellement définis dans les sources Java.

## 🛠️ Compiler le plugin

Installez un **JDK 21** et **Maven 3.9+**, puis exécutez ces commandes depuis un terminal :

```bash
git clone https://github.com/Skyzenfr/VanillaPlus.git
cd VanillaPlus
java -version
mvn -version
mvn -f VanillaPlus/pom.xml clean package
```

Le JAR installable est généré dans `VanillaPlus/target/VanillaPlus-1.0.jar`. La release le distribue sous le nom `VanillaPlus-1.0-mc1.21.jar` pour rendre la cible Minecraft explicite.

La compilation nécessite un accès aux dépôts Maven pour télécharger l'API Spigot et les bibliothèques Adventure. Spigot est une dépendance `provided` ; les bibliothèques Adventure nécessaires sont intégrées au JAR par Maven Shade. Ne distribuez pas le JAR `original-*` produit pendant cette étape.

<details>
<summary><strong>Structure du dépôt</strong></summary>

```text
VanillaPlus/
├── README.md
├── docs/assets/                    # Visuels du README
└── VanillaPlus/
    ├── pom.xml                     # Compilation Maven / Java 21
    └── src/main/
        ├── java/fr/skyzen/vanillaplus/
        │   ├── commands/           # Commandes et autocomplétion
        │   ├── listener/           # Événements joueurs, monde et marché
        │   ├── manager/            # Enregistrement des listeners et marché
        │   └── utils/              # Données, économie, téléportations et GUI
        └── resources/              # plugin.yml et fichiers YAML par défaut
```

</details>

## 🔎 Dépannage et limites actuelles

| Symptôme | Vérification |
| :--- | :--- |
| Le plugin ne se charge pas | Utilisez Java 21, un serveur ciblant Minecraft 1.21 et le JAR de la release, puis relevez la première erreur dans `logs/latest.log`. |
| Une commande est inconnue ou provient d'un autre plugin | Vérifiez le démarrage du plugin, les alias et les conflits de commandes. Certaines commandes natives sont volontairement bloquées. |
| `/spawn` ne fonctionne pas | Vérifiez l'existence du monde nommé `world`, utilisé explicitement par le code. |
| Une téléportation est annulée | Restez immobile jusqu'à la fin du compte à rebours ou adaptez `teleport.cancelOnMove`. |
| La compilation indique `release version 21 not supported` | Vérifiez que `mvn -version` utilise un JDK 21. |

Cette version présente encore des limites fonctionnelles :

- **Messages privés :** les alias de `/msg` sont déclarés mais le gestionnaire vérifie le nom saisi. Utilisez `/msg`. `/reply` utilise actuellement une instance distincte du gestionnaire et peut ne pas retrouver le dernier interlocuteur.
- **Warps :** après création du tout premier warp, un redémarrage peut être nécessaire pour qu'il apparaisse dans le menu.
- **Économie hors ligne :** l'accès aux données persistantes est limité aux joueurs connectés. Les soldes affichés pour des joueurs déconnectés et le crédit des ventes lorsque le vendeur est absent ne sont pas fiables dans cette implémentation.
- **Annonces expirées :** les objets sont rendus seulement si le vendeur est connecté lors du nettoyage. Une annonce expirée peut donc être supprimée sans restitution lorsque son vendeur est hors ligne.
- **Récompenses :** le plafond nommé « quotidien » dans le code est conservé en mémoire, sans réinitialisation journalière programmée. Il ne faut pas le considérer comme une limite calendaire persistante.

Pour signaler un problème, ouvrez une [issue](https://github.com/Skyzenfr/VanillaPlus/issues) avec la version du serveur, celle de Java, les étapes de reproduction et les lignes de log utiles. Évitez d'y joindre des messages privés ou des données personnelles.

## 🤝 Contribuer

Les corrections et améliorations de documentation sont les bienvenues. Ouvrez une issue pour discuter d'un changement important, puis créez une branche depuis votre fork, compilez avec Java 21 et proposez une Pull Request décrivant le comportement et les vérifications réalisées.

Avant de proposer une modification du gameplay, essayez le parcours concerné sur un serveur 1.21 de test : chargement du plugin, commandes, interfaces et conservation des données après redémarrage.

## 📄 Licence et auteur

Développé par [**Skyzen**](https://github.com/Skyzenfr).

Ce dépôt ne contient actuellement aucun fichier `LICENSE`. Aucune licence de réutilisation n'est donc explicitement fournie ; contactez l'auteur pour les conditions de réutilisation et de redistribution.

---

<p align="center">
  <strong>VanillaPlus</strong><br>
  Une base de survie à faire grandir ensemble. 🌱
</p>
