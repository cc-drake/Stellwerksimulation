Hinweise zu Strecken.csv:
StreckenID's m�ssen eindeutig sein.
Betriebsstellen sind Endpunkte von Fahrwegen und d�rfen nicht mehr als zwei ausgehende Strecken besitzen.
Die Lage ist matrixcodiert (d.h. links oben ist (0,0).
Die Anschlussrichtung gibt an, in welche Richtungen eine Strecke verl�uft, und werden mit / getrennt. Beispiel: "O/W" f�r eine Ost-West-Strecke. 
Um auf einer Strecke eine kreuzungsfreie Br�cke zu zeichnen, m�ssen zwei Strecken mit identischen Koordinaten angelegt werden. F�r beide Strecken ist dann im Feld "Br�ckenebene (oben/unten) der Eintrag "oben" bzw. "unten" vorzunehmen.
Die Positionsangaben zu Z�gen und Bahnhofsnamen erm�glichen die Verschiebung der Textbox in Pixeln.

Hinweise zu Fahrwege.csv:
F�r jeden Fahrweg muss angegeben sein, welche Fahrziele hier�ber erreicht werden k�nnen. Die Fahrziele m�ssen in der �berschriftenzeile verzeichnet werden. Hierzu gibt es 3 M�glichkeiten:
- Der Eintrag "b" bedeutet, dass der Weg zu diesem Fahrziel �ber den Bahnhof f�hrt. Z�ge mit diesem Fahrziel k�nnen diesen Fahrweg daher problemlos nutzen.
- Der Eintrag "x" bedeutet, dass zwar ein Weg zum Bahnhof vorhanden ist, dieser jedoch nicht �ber einen Bahnhof f�hrt. Daher werden Z�ge mit diesem Fahrziel diesen Fahrweg nur nutzen, wenn sie den Fahrgastwechsel bereits erledigt haben oder G�terz�ge sind.
- Ein leerer Eintrag bedeutet, dass dieses Fahrziel nicht �ber diesen Fahrweg erreichbar ist. Entsprechend werden Z�ge mit diesem Fahrziel diesen Fahrweg nicht nutzen k�nnen.
Hinweis: Rangierende Kurswagen k�nnen unabh�ngig von ihrem Fahrtziel jeden Fahrweg nutzen.
In den weiteren Spalten (Ab �berschrift "Laufweg") wird der Verlauf des Fahrweges definiert. Die Angabe einer Fahrtrichtung ist f�r kreuzungsfreie Strecken optional, andernfalls hat die Richtungsangabe mit einem "$" an die entsprechende Strecke angeh�ngt zu werden. Beispiel: Strecke 1a$O.
Die Richtungscodierung ist wie bei den Strecken, d.h. Nord=N, Nordost=NO,...
Es ist zu beachten, dass ein Fahrweg jede Strecke nur einmal ber�hren darf.
Das Einf�gen freier Zeilen zwischen einzelnen Fahrwegsbl�cken ist der �bersichtlichkeit halber erlaubt.

Hinweise zu Fahrplan.csv:
Im Feld Gleis ist der Name der Betriebsstelle anzugeben, an der der Fahrgastwechsel planm��ig stattfinden soll.
F�r G�terz�ge oder andere Z�ge ohne Halt ist das Feld "Gleis" leer zu lassen.