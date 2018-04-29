# Ghost-Android-Game

</b>"Ghost is a word game in which players take turns adding letters to a growing word fragment, trying not to be the one to complete a valid word."</b>

System Details : <br>
Android Studio 2.3.3 <br>
Build : 162.4069837 <br>
JRE : 1.8 <br>

<h2>Screenshot of App :</h2>

<p float="left">
<img src="ghost.gif" width="250"/>
/* <img src="ghost.gif" width="250"/> */
</p>

<h3>Game Rules: </h3>
<ol>
  <li>Ghost is a word game in which players take turns adding letters to a growing word fragment, trying not to be the one to complete a valid word.</li>
  <li>Each incomplete wordmust be the beginning of an actual word, and  minmum length of a word that counts is 4 letters.</li>
  <li>The player who completes a word lose the round.</li>
  <li>A player is chosen at random to start the game, and begins by naming any letter of the alphabet.</li>
  <li>Players then take turns to add letters to this fragment, with the aim being to avoid completing an actual word.</li>
  <li>The player whose turn it is may :- 
    <ul>
    <li> Instead of adding a letter, challenge the previous player to prove that the current fragment is actually the beginning of a word.</li>
    <li> If the challenged player can name such a word, the challenger loses the round; otherwise the challenged player loses the round.</li>
    <li> If a player bluffs, or completes a word without other players noticing, then play continues.</li>
    </ul>
  </li>     
</ol>

<h2>Ghost Strategy :-</h2>
<h5>A Sample Dictionary :-</h5>
<ol>
<li>there</li>
<li>any</li>
<li>answer</li>
<li>anyone</li>
<li>their</li>
<li>bye</li>
</ol>


Markov Model from Dictionary :- 
<table>
 <th>Current State</th>
 <th>Possible next character</th>
 <tr> <td>the</td>
     <td>{'r':1, 'i':1}</td>
 </tr>
 <tr> <td>her</td>
     <td>{'e':1}</td>
 </tr>
 <tr> <td>ans</td>
     <td>{'w':1}</td>
 </tr>
  <tr> <td>nsw</td>
     <td>{'e':1}</td>
 </tr>
  <tr> <td>swe</td>
     <td>{'r':1}</td>
 </tr>
  <tr> <td>any</td>
     <td>{'o':1}</td>
 </tr>
  <tr> <td>nyo</td>
     <td>{'n':1}</td>
 </tr>
  <tr> <td>yon</td>
     <td>{'e':1}</td>
 </tr>
  <tr> <td>hei</td>
     <td>{'r':1}</td>
 </tr>
</table>

<h5>Ghost will use markov model to predict next char of word fragment.</h5>
Order of Markov model is 3, which means next character wil be decided from last 3 character.

<h2>How to check current word fragment is valid or complete :- </h2>
<h4> 1. Binary Search :- </h4>
  <ul>
    <li>Sort Dictionary lexicographically</li>
    <li>Search for current word in sorted dictionary using Binary search.</li>
    <li>Complexity : O( log<sub>2</sub>(N)) where N is number of word in dictionary.</li>
  </ul>
<h4> 2. Trie :- </h4>
  <ul>
    <li>The trie is a tree where each vertex represents a single word or a prefix.</li>
    <li>The tries can insert and find strings in O(L) time (where L represent the length of a single word). This is much faster than Binary search.</li>
  </ul>
  <h5>How Trie look like :- </h5>
  <img src="tries.gif">
  
  
 
