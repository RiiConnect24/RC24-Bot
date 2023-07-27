/*
 * MIT License
 *
 * Copyright (c) 2017-2020 RiiConnect24 and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package xyz.rc24.bot.core.entities;

import org.jetbrains.annotations.NotNull;

public enum Flag {
    AFGHANISTAN("\uD83C\uDDE6\uD83C\uDDEB", "Afghanistan"),
    ALAND_ISLANDS("\uD83C\uDDE6\uD83C\uDDFD", "Aland Islands"),
    ALBANIA("\uD83C\uDDE6\uD83C\uDDF1", "Albania"),
    ALGERIA("\uD83C\uDDE9\uD83C\uDDFF", "Algeria"),
    AMERICAN_SAMOA("\uD83C\uDDE6\uD83C\uDDF8", "American Samoa"),
    ANDORRA("\uD83C\uDDE6\uD83C\uDDE9", "Andorra"),
    ANGOLA("\uD83C\uDDE6\uD83C\uDDF4", "Angola"),
    ANGUILLA("\uD83C\uDDE6\uD83C\uDDEE", "Anguilla"),
    ANTARCTICA("\uD83C\uDDE6\uD83C\uDDF6", "Antarctica"),
    ANTIGUA_BARBUDA("\uD83C\uDDE6\uD83C\uDDEC", "Antigua and Barbuda"),
    ARGENTINA("\uD83C\uDDE6\uD83C\uDDF7", "Argentina"),
    ARMENIA("\uD83C\uDDE6\uD83C\uDDF2", "Armenia"),
    ARUBA("\uD83C\uDDE6\uD83C\uDDFC", "Aruba"),
    ASCENSION_ISLAND("\uD83C\uDDE6\uD83C\uDDE8", "Ascension Island"),
    AUSTRALIA("\uD83C\uDDE6\uD83C\uDDFA", "Australia"),
    AUSTRIA("\uD83C\uDDE6\uD83C\uDDF9", "Austria"),
    AZERBAIJAN("\uD83C\uDDE6\uD83C\uDDFF", "Azerbaijan"),
    BAHAMAS("\uD83C\uDDE7\uD83C\uDDF8", "Bahamas"),
    BAHRAIN("\uD83C\uDDE7\uD83C\uDDED", "Bahrain"),
    BANGLADESH("\uD83C\uDDE7\uD83C\uDDE9", "Bangladesh"),
    BARBADOS("\uD83C\uDDE7\uD83C\uDDE7", "Barbados"),
    BELARUS("\uD83C\uDDE7\uD83C\uDDFE", "Belarus"),
    BELGIUM("\uD83C\uDDE7\uD83C\uDDEA", "Belgium"),
    BELIZE("\uD83C\uDDE7\uD83C\uDDFF", "Belize"),
    BENIN("\uD83C\uDDE7\uD83C\uDDEF", "Benin"),
    BERMUDA("\uD83C\uDDE7\uD83C\uDDF2", "Bermuda"),
    BHUTAN("\uD83C\uDDE7\uD83C\uDDF9", "Bhutan"),
    BOLIVIA("\uD83C\uDDE7\uD83C\uDDF4", "Bolivia"),
    BOSNIA_HERZEGOVINA("\uD83C\uDDE7\uD83C\uDDE6", "Bosnia Herzegovina"),
    BOTSWANA("\uD83C\uDDE7\uD83C\uDDFC", "Botswana"),
    BOUVET_ISLAND("\uD83C\uDDE7\uD83C\uDDFB", "Bouvet Island"),
    BRAZIL("\uD83C\uDDE7\uD83C\uDDF7", "Brazil"),
    BRITISH_INDIAN_OCEAN_TERRITORY("\uD83C\uDDEE\uD83C\uDDF4", "British Indian Ocean Territory"),
    BRITISH_VIRGIN_ISLANDS("\uD83C\uDDFB\uD83C\uDDEC", "British Virgin Islands"),
    BRUNEI("\uD83C\uDDE7\uD83C\uDDF3", "Brunei"),
    BULGARIA("\uD83C\uDDE7\uD83C\uDDEC", "Bulgaria"),
    BURKINA_FASO("\uD83C\uDDE7\uD83C\uDDEB", "Burkina Faso"),
    BURUNDI("\uD83C\uDDE7\uD83C\uDDEE", "Burundi"),
    CAMBODIA("\uD83C\uDDF0\uD83C\uDDED", "Cambodia"),
    CAMEROON("\uD83C\uDDE8\uD83C\uDDF2", "Cameroon"),
    CANADA("\uD83C\uDDE8\uD83C\uDDE6", "Canada"),
    CANARY_ISLANDS("\uD83C\uDDEE\uD83C\uDDE8", "Canary Islands"),
    CAPE_VERDE("\uD83C\uDDE8\uD83C\uDDFB", "Cape Verde"),
    CARIBBEAN_NETHERLANDS("\uD83C\uDDE7\uD83C\uDDF6", "Caribbean Netherlands"),
    CAYMAN_ISLANDS("\uD83C\uDDF0\uD83C\uDDFE", "Cayman Islands"),
    CENTRAL_AFRICAN_REPUBLIC("\uD83C\uDDE8\uD83C\uDDEB", "Central African Republic"),
    CEUTA_MELILLA("\uD83C\uDDEA\uD83C\uDDE6", "Ceuta Melilla"),
    CHAD("\uD83C\uDDF9\uD83C\uDDE9", "Chad"),
    CHILE("\uD83C\uDDE8\uD83C\uDDF1", "Chile"),
    CHRISTMAS_ISLAND("\uD83C\uDDE8\uD83C\uDDFD", "Christmas Island"),
    CLIPPERTON_ISLAND("\uD83C\uDDE8\uD83C\uDDF5", "Clipperton Island"),
    CN("\uD83C\uDDE8\uD83C\uDDF3", "Cn"),
    COCOS_ISLANDS("\uD83C\uDDE8\uD83C\uDDE8", "Cocos Islands"),
    COLOMBIA("\uD83C\uDDE8\uD83C\uDDF4", "Colombia"),
    COMOROS("\uD83C\uDDF0\uD83C\uDDF2", "Comoros"),
    CONGO_BRAZZAVILLE("\uD83C\uDDE8\uD83C\uDDEC", "Congo Brazzaville"),
    CONGO_KINSHASA("\uD83C\uDDE8\uD83C\uDDE9", "Congo Kinshasa"),
    COOK_ISLANDS("\uD83C\uDDE8\uD83C\uDDF0", "Cook Islands"),
    COSTA_RICA("\uD83C\uDDE8\uD83C\uDDF7", "Costa Rica"),
    COTE_DIVOIRE("\uD83C\uDDE8\uD83C\uDDEE", "Cote D'Ivoire"),
    CROATIA("\uD83C\uDDED\uD83C\uDDF7", "Croatia"),
    CUBA("\uD83C\uDDE8\uD83C\uDDFA", "Cuba"),
    CURACAO("\uD83C\uDDE8\uD83C\uDDFC", "Curacao"),
    CYPRUS("\uD83C\uDDE8\uD83C\uDDFE", "Cyprus"),
    CZECH_REPUBLIC("\uD83C\uDDE8\uD83C\uDDFF", "Czech Republic"),
    DENMARK("\uD83C\uDDE9\uD83C\uDDF0", "Denmark"),
    DIEGO_GARCIA("\uD83C\uDDE9\uD83C\uDDEC", "Diego Garcia"),
    DJIBOUTI("\uD83C\uDDE9\uD83C\uDDEF", "Djibouti"),
    DOMINICA("\uD83C\uDDE9\uD83C\uDDF2", "Dominica"),
    DOMINICAN_REPUBLIC("\uD83C\uDDE9\uD83C\uDDF4", "Dominican Republic"),
    ECUADOR("\uD83C\uDDEA\uD83C\uDDE8", "Ecuador"),
    EGYPT("\uD83C\uDDEA\uD83C\uDDEC", "Egypt"),
    EL_SALVADOR("\uD83C\uDDF8\uD83C\uDDFB", "El Salvador"),
    ENGLAND("\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC65\uDB40\uDC6E\uDB40\uDC67\uDB40\uDC7F", "England"),
    EQUATORIAL_GUINEA("\uD83C\uDDEC\uD83C\uDDF6", "Equatorial Guinea"),
    ERITREA("\uD83C\uDDEA\uD83C\uDDF7", "Eritrea"),
    ESTONIA("\uD83C\uDDEA\uD83C\uDDEA", "Estonia"),
    ETHIOPIA("\uD83C\uDDEA\uD83C\uDDF9", "Ethiopia"),
    EUROPEAN_UNION("\uD83C\uDDEA\uD83C\uDDFA", "European Union"),
    FALKLAND_ISLANDS("\uD83C\uDDEB\uD83C\uDDF0", "Falkland Islands"),
    FAROE_ISLANDS("\uD83C\uDDEB\uD83C\uDDF4", "Faroe Islands"),
    FIJI("\uD83C\uDDEB\uD83C\uDDEF", "Fiji"),
    FINLAND("\uD83C\uDDEB\uD83C\uDDEE", "Finland"),
    France("\uD83C\uDDEB\uD83C\uDDF7", "France"),
    FRENCH_GUIANA("\uD83C\uDDEC\uD83C\uDDEB", "French Guiana"),
    FRENCH_POLYNESIA("\uD83C\uDDF5\uD83C\uDDEB", "French Polynesia"),
    FRENCH_SOUTHERN_TERRITORIES("\uD83C\uDDF9\uD83C\uDDEB", "French Southern Territories"),
    GABON("\uD83C\uDDEC\uD83C\uDDE6", "Gabon"),
    GAMBIA("\uD83C\uDDEC\uD83C\uDDF2", "Gambia"),
    GEORGIA("\uD83C\uDDEC\uD83C\uDDEA", "Georgia"),
    GERMANY("\uD83C\uDDE9\uD83C\uDDEA", "Germany"),
    GHANA("\uD83C\uDDEC\uD83C\uDDED", "Ghana"),
    GIBRALTAR("\uD83C\uDDEC\uD83C\uDDEE", "Gibraltar"),
    GREECE("\uD83C\uDDEC\uD83C\uDDF7", "Greece"),
    GREENLAND("\uD83C\uDDEC\uD83C\uDDF1", "Greenland"),
    GRENADA("\uD83C\uDDEC\uD83C\uDDE9", "Grenada"),
    GUADELOUPE("\uD83C\uDDEC\uD83C\uDDF5", "Guadeloupe"),
    GUAM("\uD83C\uDDEC\uD83C\uDDFA", "Guam"),
    GUATEMALA("\uD83C\uDDEC\uD83C\uDDF9", "Guatemala"),
    GUERNSEY("\uD83C\uDDEC\uD83C\uDDEC", "Guernsey"),
    GUINEA("\uD83C\uDDEC\uD83C\uDDF3", "Guinea"),
    GUINEA_BISSAU("\uD83C\uDDEC\uD83C\uDDFC", "Guinea Bissau"),
    GUYANA("\uD83C\uDDEC\uD83C\uDDFE", "Guyana"),
    HAITI("\uD83C\uDDED\uD83C\uDDF9", "Haiti"),
    HEARD_MCDONALD_ISLANDS("\uD83C\uDDED\uD83C\uDDF2", "Heard McDonald Islands"),
    HONDURAS("\uD83C\uDDED\uD83C\uDDF3", "Honduras"),
    HONG_KONG("\uD83C\uDDED\uD83C\uDDF0", "Hong Kong"),
    HUNGARY("\uD83C\uDDED\uD83C\uDDFA", "Hungary"),
    ICELAND("\uD83C\uDDEE\uD83C\uDDF8", "Iceland"),
    INDIA("\uD83C\uDDEE\uD83C\uDDF3", "India"),
    INDONESIA("\uD83C\uDDEE\uD83C\uDDE9", "Indonesia"),
    IRAN("\uD83C\uDDEE\uD83C\uDDF7", "Iran"),
    IRAQ("\uD83C\uDDEE\uD83C\uDDF6", "Iraq"),
    IRELAND("\uD83C\uDDEE\uD83C\uDDEA", "Ireland"),
    ISLE_OF_MAN("\uD83C\uDDEE\uD83C\uDDF2", "Isle of Man"),
    ISRAEL("\uD83C\uDDEE\uD83C\uDDF1", "Israel"),
    ITALY("\uD83C\uDDEE\uD83C\uDDF9", "Italy"),
    JAMAICA("\uD83C\uDDEF\uD83C\uDDF2", "Jamaica"),
    JAPAN("\uD83C\uDDEF\uD83C\uDDF5", "Japan"),
    JERSEY("\uD83C\uDDEF\uD83C\uDDEA", "Jersey"),
    JORDAN("\uD83C\uDDEF\uD83C\uDDF4", "Jordan"),
    KAZAKHSTAN("\uD83C\uDDF0\uD83C\uDDFF", "Kazakhstan"),
    KENYA("\uD83C\uDDF0\uD83C\uDDEA", "Kenya"),
    KIRIBATI("\uD83C\uDDF0\uD83C\uDDEE", "Kiribati"),
    KOSOVO("\uD83C\uDDFD\uD83C\uDDF0", "Kosovo"),
    KUWAIT("\uD83C\uDDF0\uD83C\uDDFC", "Kuwait"),
    KYRGYZSTAN("\uD83C\uDDF0\uD83C\uDDEC", "Kyrgyzstan"),
    LAOS("\uD83C\uDDF1\uD83C\uDDE6", "Laos"),
    LATVIA("\uD83C\uDDF1\uD83C\uDDFB", "Latvia"),
    LEBANON("\uD83C\uDDF1\uD83C\uDDE7", "Lebanon"),
    LESOTHO("\uD83C\uDDF1\uD83C\uDDF8", "Lesotho"),
    LIBERIA("\uD83C\uDDF1\uD83C\uDDF7", "Liberia"),
    LIBYA("\uD83C\uDDF1\uD83C\uDDFE", "Libya"),
    LIECHTENSTEIN("\uD83C\uDDF1\uD83C\uDDEE", "Liechtenstein"),
    LITHUANIA("\uD83C\uDDF1\uD83C\uDDF9", "Lithuania"),
    LUXEMBOURG("\uD83C\uDDF1\uD83C\uDDFA", "Luxembourg"),
    MACAU("\uD83C\uDDF2\uD83C\uDDF4", "Macau"),
    MACEDONIA("\uD83C\uDDF2\uD83C\uDDF0", "Macedonia"),
    MADAGASCAR("\uD83C\uDDF2\uD83C\uDDEC", "Madagascar"),
    MALAWI("\uD83C\uDDF2\uD83C\uDDFC", "Malawi"),
    MALAYSIA("\uD83C\uDDF2\uD83C\uDDFE", "Malaysia"),
    MALDIVES("\uD83C\uDDF2\uD83C\uDDFB", "Maldives"),
    MALI("\uD83C\uDDF2\uD83C\uDDF1", "Mali"),
    MALTA("\uD83C\uDDF2\uD83C\uDDF9", "Malta"),
    MARSHALL_ISLANDS("\uD83C\uDDF2\uD83C\uDDED", "Marshall Islands"),
    MARTINIQUE("\uD83C\uDDF2\uD83C\uDDF6", "Martinique"),
    MAURITANIA("\uD83C\uDDF2\uD83C\uDDF7", "Mauritania"),
    MAURITIUS("\uD83C\uDDF2\uD83C\uDDFA", "Mauritius"),
    MAYOTTE("\uD83C\uDDFE\uD83C\uDDF9", "Mayotte"),
    MEXICO("\uD83C\uDDF2\uD83C\uDDFD", "Mexico"),
    MICRONESIA("\uD83C\uDDEB\uD83C\uDDF2", "Micronesia"),
    MOLDOVA("\uD83C\uDDF2\uD83C\uDDE9", "Moldova"),
    MONACO("\uD83C\uDDF2\uD83C\uDDE8", "Monaco"),
    MONGOLIA("\uD83C\uDDF2\uD83C\uDDF3", "Mongolia"),
    MONTENEGRO("\uD83C\uDDF2\uD83C\uDDEA", "Montenegro"),
    MONTSERRAT("\uD83C\uDDF2\uD83C\uDDF8", "Montserrat"),
    MOROCCO("\uD83C\uDDF2\uD83C\uDDE6", "Morocco"),
    MOZAMBIQUE("\uD83C\uDDF2\uD83C\uDDFF", "Mozambique"),
    MYANMAR("\uD83C\uDDF2\uD83C\uDDF2", "Myanmar"),
    NAMIBIA("\uD83C\uDDF3\uD83C\uDDE6", "Namibia"),
    NAURU("\uD83C\uDDF3\uD83C\uDDF7", "Nauru"),
    NEPAL("\uD83C\uDDF3\uD83C\uDDF5", "Nepal"),
    NETHERLANDS("\uD83C\uDDF3\uD83C\uDDF1", "Netherlands"),
    NEW_CALEDONIA("\uD83C\uDDF3\uD83C\uDDE8", "New Caledonia"),
    NEW_ZEALAND("\uD83C\uDDF3\uD83C\uDDFF", "New Zealand"),
    NICARAGUA("\uD83C\uDDF3\uD83C\uDDEE", "Nicaragua"),
    NIGER("\uD83C\uDDF3\uD83C\uDDEA", "Niger"),
    NIGERIA("\uD83C\uDDF3\uD83C\uDDEC", "Nigeria"),
    NIUE("\uD83C\uDDF3\uD83C\uDDFA", "Niue"),
    NORFOLK_ISLAND("\uD83C\uDDF3\uD83C\uDDEB", "Norfolk Island"),
    NORTH_KOREA("\uD83C\uDDF0\uD83C\uDDF5", "North Korea"),
    NORTHERN_MARIANA_ISLANDS("\uD83C\uDDF2\uD83C\uDDF5", "Northern Mariana Islands"),
    NORWAY("\uD83C\uDDF3\uD83C\uDDF4", "Norway"),
    OMAN("\uD83C\uDDF4\uD83C\uDDF2", "Oman"),
    PAKISTAN("\uD83C\uDDF5\uD83C\uDDF0", "Pakistan"),
    PALAU("\uD83C\uDDF5\uD83C\uDDFC", "Palau"),
    PALESTINIAN_TERRITORIES("\uD83C\uDDF5\uD83C\uDDF8", "Palestinian Territories"),
    PANAMA("\uD83C\uDDF5\uD83C\uDDE6", "Panama"),
    PAPUA_NEW_GUINEA("\uD83C\uDDF5\uD83C\uDDEC", "Papua New Guinea"),
    PARAGUAY("\uD83C\uDDF5\uD83C\uDDFE", "Paraguay"),
    PERU("\uD83C\uDDF5\uD83C\uDDEA", "Peru"),
    PHILIPPINES("\uD83C\uDDF5\uD83C\uDDED", "Philippines"),
    PIRATE_FLAG("\uD83C\uDFF4\u200D\u2620\uFE0F", "Pirate"),
    PITCAIRN_ISLANDS("\uD83C\uDDF5\uD83C\uDDF3", "Pitcairn Islands"),
    POLAND("\uD83C\uDDF5\uD83C\uDDF1", "Poland"),
    PORTUGAL("\uD83C\uDDF5\uD83C\uDDF9", "Portugal"),
    PUERTO_RICO("\uD83C\uDDF5\uD83C\uDDF7", "Puerto Rico"),
    QATAR("\uD83C\uDDF6\uD83C\uDDE6", "Qatar"),
    RAINBOW_FLAG("\uD83C\uDFF3\uFE0F\u200D\uD83C\uDF08", "Pride"),
    REUNION("\uD83C\uDDF7\uD83C\uDDEA", "Reunion"),
    ROMANIA("\uD83C\uDDF7\uD83C\uDDF4", "Romania"),
    RUSSIA("\uD83C\uDDF7\uD83C\uDDFA", "Russia"),
    RWANDA("\uD83C\uDDF7\uD83C\uDDFC", "Rwanda"),
    SAMOA("\uD83C\uDDFC\uD83C\uDDF8", "Samoa"),
    SAN_MARINO("\uD83C\uDDF8\uD83C\uDDF2", "San Marino"),
    SAO_TOME_PRINCIPE("\uD83C\uDDF8\uD83C\uDDF9", "Sao Tome Principe"),
    SAUDI_ARABIA("\uD83C\uDDF8\uD83C\uDDE6", "Saudi Arabia"),
    SCOTLAND("\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC73\uDB40\uDC63\uDB40\uDC74\uDB40\uDC7F", "Scotland"),
    SENEGAL("\uD83C\uDDF8\uD83C\uDDF3", "Senegal"),
    SERBIA("\uD83C\uDDF7\uD83C\uDDF8", "Serbia"),
    SEYCHELLES("\uD83C\uDDF8\uD83C\uDDE8", "Seychelles"),
    SIERRA_LEONE("\uD83C\uDDF8\uD83C\uDDF1", "Sierra Leone"),
    SINGAPORE("\uD83C\uDDF8\uD83C\uDDEC", "Singapore"),
    SINT_MAARTEN("\uD83C\uDDF8\uD83C\uDDFD", "Sint Maarten"),
    SLOVAKIA("\uD83C\uDDF8\uD83C\uDDF0", "Slovakia"),
    SLOVENIA("\uD83C\uDDF8\uD83C\uDDEE", "Slovenia"),
    SOLOMON_ISLANDS("\uD83C\uDDF8\uD83C\uDDE7", "Solomon Islands"),
    SOMALIA("\uD83C\uDDF8\uD83C\uDDF4", "Somalia"),
    SOUTH_AFRICA("\uD83C\uDDFF\uD83C\uDDE6", "South Africa"),
    SOUTH_GEORGIA_SOUTH_SANDWICH_ISLANDS("\uD83C\uDDEC\uD83C\uDDF8", "South Georgia South Sandwich Islands"),
    SOUTH_KOREA("\uD83C\uDDF0\uD83C\uDDF7", "South Korea"),
    SOUTH_SUDAN("\uD83C\uDDF8\uD83C\uDDF8", "South Sudan"),
    Spain("\uD83C\uDDEA\uD83C\uDDF8", "Spain"),
    SRI_LANKA("\uD83C\uDDF1\uD83C\uDDF0", "Sri Lanka"),
    ST_BARTHELEMY("\uD83C\uDDE7\uD83C\uDDF1", "St. Barthelemy"),
    ST_HELENA("\uD83C\uDDF8\uD83C\uDDED", "St. Helena"),
    ST_KITTS_NEVIS("\uD83C\uDDF0\uD83C\uDDF3", "St. Kitts and Nevis"),
    ST_LUCIA("\uD83C\uDDF1\uD83C\uDDE8", "St. Lucia"),
    ST_MARTIN("\uD83C\uDDF2\uD83C\uDDEB", "St. Martin"),
    ST_PIERRE_MIQUELON("\uD83C\uDDF5\uD83C\uDDF2", "St. Pierre Miquelon"),
    ST_VINCENT_GRENADINES("\uD83C\uDDFB\uD83C\uDDE8", "St. Vincent and Grenadines"),
    SUDAN("\uD83C\uDDF8\uD83C\uDDE9", "Sudan"),
    SURINAME("\uD83C\uDDF8\uD83C\uDDF7", "Suriname"),
    SVALBARD_JAN_MAYEN("\uD83C\uDDF8\uD83C\uDDEF", "Svalbard Jan Mayen"),
    SWAZILAND("\uD83C\uDDF8\uD83C\uDDFF", "Swaziland"),
    SWEDEN("\uD83C\uDDF8\uD83C\uDDEA", "Sweden"),
    SWITZERLAND("\uD83C\uDDE8\uD83C\uDDED", "Switzerland"),
    SYRIA("\uD83C\uDDF8\uD83C\uDDFE", "Syria"),
    TAIWAN("\uD83C\uDDF9\uD83C\uDDFC", "Taiwan"),
    TAJIKISTAN("\uD83C\uDDF9\uD83C\uDDEF", "Tajikistan"),
    TANZANIA("\uD83C\uDDF9\uD83C\uDDFF", "Tanzania"),
    THAILAND("\uD83C\uDDF9\uD83C\uDDED", "Thailand"),
    TIMOR_LESTE("\uD83C\uDDF9\uD83C\uDDF1", "Timor Leste"),
    TOGO("\uD83C\uDDF9\uD83C\uDDEC", "Togo"),
    TOKELAU("\uD83C\uDDF9\uD83C\uDDF0", "Tokelau"),
    TONGA("\uD83C\uDDF9\uD83C\uDDF4", "Tonga"),
    TRANSGENDER_FLAG("\uD83C\uDFF3\uFE0F\u200D\u26A7\uFE0F", "Transgender"),
    TRINIDAD_TOBAGO("\uD83C\uDDF9\uD83C\uDDF9", "Trinidad and Tobago"),
    TRISTAN_DA_CUNHA("\uD83C\uDDF9\uD83C\uDDE6", "Tristan Da Cunha"),
    TUNISIA("\uD83C\uDDF9\uD83C\uDDF3", "Tunisia"),
    TURKEY("\uD83C\uDDF9\uD83C\uDDF7", "Turkey"),
    TURKMENISTAN("\uD83C\uDDF9\uD83C\uDDF2", "Turkmenistan"),
    TURKS_CAICOS_ISLANDS("\uD83C\uDDF9\uD83C\uDDE8", "Turks and Caicos Islands"),
    TUVALU("\uD83C\uDDF9\uD83C\uDDFB", "Tuvalu"),
    UGANDA("\uD83C\uDDFA\uD83C\uDDEC", "Uganda"),
    UKRAINE("\uD83C\uDDFA\uD83C\uDDE6", "Ukraine"),
    UNITED_ARAB_EMIRATES("\uD83C\uDDE6\uD83C\uDDEA", "United Arab Emirates"),
    UNITED_KINGDOM("\uD83C\uDDEC\uD83C\uDDE7", "United Kingdom"),
    UNITED_NATIONS("\uD83C\uDDFA\uD83C\uDDF3", "United Nations"),
    URUGUAY("\uD83C\uDDFA\uD83C\uDDFE", "Uruguay"),
    USA("\uD83C\uDDFA\uD83C\uDDF8", "United States"),
    US_OUTLYING_ISLANDS("\uD83C\uDDFA\uD83C\uDDF2", "US Outlying Islands"),
    US_VIRGIN_ISLANDS("\uD83C\uDDFB\uD83C\uDDEE", "US Virgin Islands"),
    UZBEKISTAN("\uD83C\uDDFA\uD83C\uDDFF", "Uzbekistan"),
    VANUATU("\uD83C\uDDFB\uD83C\uDDFA", "Vanuatu"),
    VATICAN_CITY("\uD83C\uDDFB\uD83C\uDDE6", "Vatican City"),
    VENEZUELA("\uD83C\uDDFB\uD83C\uDDEA", "Venezuela"),
    VIETNAM("\uD83C\uDDFB\uD83C\uDDF3", "Vietnam"),
    WALES("\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC77\uDB40\uDC6C\uDB40\uDC73\uDB40\uDC7F", "Wales"),
    WALLIS_FUTUNA("\uD83C\uDDFC\uD83C\uDDEB", "Wallis Futuna"),
    WESTERN_SAHARA("\uD83C\uDDEA\uD83C\uDDED", "Western Sahara"),
    YEMEN("\uD83C\uDDFE\uD83C\uDDEA", "Yemen"),
    ZAMBIA("\uD83C\uDDFF\uD83C\uDDF2", "Zambia"),
    ZIMBABWE("\uD83C\uDDFF\uD83C\uDDFC", "Zimbabwe");

    private final String emote, name;

    Flag(String emote, String name) {
        this.emote = emote;
        this.name = name;
    }

    public String getEmote() {
        return emote;
    }

    public String getName() {
        return name;
    }

    public static Flag fromEmoji(String emote) {
        if (emote != null) {
            for (Flag flag : values()) {
                if (emote.equals(flag.emote)) {
                    return flag;
                }
            }
        }
        return null;
    }

    public static Flag fromName(@NotNull String name) {
        for (Flag flag : values()) {
            if (!(flag.getName() == null) && name.toLowerCase().equals(flag.getName().toLowerCase()))
                return flag;
        }

        return null;
    }

    public String toString() {
        return emote + " " + name;
    }
}
