package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.person.Person;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "entry_declaration")
public class EntryDeclaration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "entry_id", nullable = false)
    private Entry entry;

    @ManyToOne(optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Column(nullable = false)
    private String signature;

    @Column(name = "signed_date", nullable = false)
    private LocalDate signedDate;

    protected EntryDeclaration() {}

    public EntryDeclaration(Entry entry, Person person, String signature, LocalDate signedDate) {
        this.entry = entry;
        this.person = person;
        this.signature = signature;
        this.signedDate = signedDate;
    }

    public Long getId() { return id; }
    public Entry getEntry() { return entry; }
    public Person getPerson() { return person; }
    public String getSignature() { return signature; }
    public LocalDate getSignedDate() { return signedDate; }
}
