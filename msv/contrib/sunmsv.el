;; sunmsv.el - XEmacs (and maybe emacs) functionality
;; for using Sun's Multi-Schema validator.
;;
;; Author - Stuart Popejoy spopejoy@pinksheets.com
;;
;; This file is not part of GNU Emacs or XEmacs.
;;
;; This program is free software; you can redistribute it and/or
;; modify it under the terms of the GNU General Public License
;; as published by the Free Software Foundation; either version 2
;; of the License, or (at your option) any later version.
;;
;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU General Public License for more details.
;;
;; You should have received a copy of the GNU General Public License
;; along with this program; if not, write to the Free Software
;; Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

;; INSTALLATION: change sunmsv-jar-path to point to where you
;; have the msv.jar file; change sunmsv-last-dtd-path to the
;; default DTD (or schema or whatever) file you use most.
;;
;; Call sunmsv-validate to validate your XML file; if you need
;; to provide a path for the java command (ie /usr/jdk/bin/java
;; or something) change the code that creates the 
;; sunmsv-valid-cmd variable.
;;
;; you can use this file, ie (load-library "sunmsv") etc, or
;; copy and paste all this stuff into your .emacs file.


(defvar sunmsv-jar-path "/opt/dev/tools/sunxml/msv.jar" 
  "Path to multi-schema validator jar file.")

(defvar sunmsv-last-dtd-path "/usr/lib/sgml/oasis-docbook-4.1.2/docbookx.dtd" 
  "Last dtd used by validator, and default dtd.")

(defun sunmsv-validate () (interactive)
  "Performs XML validation using Sun's Multi-Schema validator. Operates on saved file, not buffer."
  (setq old-insert-default-directory insert-default-directory)
  (setq sunmsv-last-dtd-path (read-file-name "Path to DTD/Schema file for validation? "
                                 (file-name-directory sunmsv-last-dtd-path)
                                 sunmsv-last-dtd-path t 
                                 (file-name-nondirectory sunmsv-last-dtd-path)))
  (setq sunmsv-valid-cmd 
        (concat "java -jar " sunmsv-jar-path " " sunmsv-last-dtd-path " "
                (buffer-file-name)))
  (setq sunmsv-regexp-alist 
        '(("^\\(Fatal \\)?[Ee]rror at line:\\([0-9,]+\\), column:\\([0-9]+\\) of file:\\(.+\\)" 4 2 3)))
  (compile-internal sunmsv-valid-cmd "No more errors" "MSV XML validation"
		    'sunmsv-validate-func
		    sunmsv-regexp-alist))


(defun sunmsv-validate-func (limit-search find-at-least)
  (goto-char limit-search)
  (setq matcher (nth 0 compilation-error-regexp-alist))
  (setq re (nth 0 matcher))
  (setq fname nil)
  (setq line nil)
  (setq col nil)
  (setq result nil)
  (if (looking-at re)
      (progn
        (setq compmarker (point-marker))
        (setq filemarker nil)
        (and (nth 1 matcher)
             (setq fname 
                   (buffer-string (match-beginning (nth 1 matcher))
                                  (match-end (nth 1 matcher)))))
        (and (nth 2 matcher)
             (setq line 
                   (buffer-string (match-beginning (nth 2 matcher))
                                  (match-end (nth 2 matcher)))))
        (and (nth 3 matcher)
             (setq col
                   (buffer-string (match-beginning (nth 3 matcher))
                                  (match-end (nth 3 matcher)))))
        ;(read-string (concat fname ":" line ":" col))
        (if (and fname 
                 (file-exists-p fname))
            (save-excursion
              (set-buffer (find-file-noselect fname))
              (setq line (sunmsv-remove-comma line))
              (if line
                  (progn 
                    (goto-line (string-to-number line))
                    (if col
                        (forward-char (string-to-number col)))))
              (setq filemarker (point-marker))))
        (setq result (cons compmarker filemarker))))
  (if result
      (setq compilation-error-list (cons result compilation-error-list))))
              
(defun sunmsv-remove-comma (org)
  (setq res org)
  (if org
      (progn
        (setq splitted (split-string org ","))
        (setq n 0)
        (setq res "")
        (while (nth n splitted)
          (setq res (concat res (nth n splitted)))
          (setq n (1+ n)))))
  res)


