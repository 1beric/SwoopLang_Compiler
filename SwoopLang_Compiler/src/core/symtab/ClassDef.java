package core.symtab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassDef extends FileDef {
	// super classes and interfaces
	private List<String> m_Parents;

	// method, constructor, and field definitions
	private Map<String, FieldDef> m_Fields;
	private List<ConstructorDef> m_Constructors;
	private Map<String, MethodDef> m_Methods;

	public ClassDef() {
		m_Fields = new HashMap<>();
		m_Constructors = new ArrayList<>();
		m_Methods = new HashMap<>();
	}

	public void parents(List<String> parents) {
		m_Parents = parents;
	}

	public boolean hasField(String id) {
		return m_Fields.containsKey(id);
	}

	public void field(FieldDef def) {
		m_Fields.put(def.id(), def);
	}

	public FieldDef field(String id) {
		return m_Fields.get(id);
	}

	public void constructor(ConstructorDef def) {
		m_Constructors.add(def);
	}

	public ConstructorDef constructor(List<String> formalTypes) {
		for (ConstructorDef def : m_Constructors) {
			List<FieldDef> formals = def.formals();
			if (formals.size() != formalTypes.size())
				continue;
			int[] i = new int[] { 0 };
			if (formals.stream().allMatch((FieldDef formal) -> {
				return formal.type().equals(formalTypes.get(i[0]++));
			}))
				return def;
		}
		return null;
	}

	public void method(MethodDef def) {
		m_Methods.put(def.id(), def);
	}

	public MethodDef method(String id) {
		return m_Methods.get(id);
	}

	@Override
	public void debugPrint() {

		System.out.println("class " + id() + " {");

		System.out.println("\tlibrary " + library());

		System.out.print("\tparents {\n\t\t");
		int numParents = 0;
		for (String id : m_Parents) {
			System.out.print(id);
			if (++numParents % 4 == 0 && numParents != m_Parents.size())
				System.out.print("\n\t\t");
			else if (numParents != m_Parents.size())
				System.out.print(", ");
		}
		System.out.println("\n\t}");

		System.out.print("\tfields {\n\t\t");
		int numFields = 0;
		for (String key : m_Fields.keySet()) {
			m_Fields.get(key).debugPrint();
			if (++numFields != m_Fields.size())
				System.out.print("\n\t\t");
		}
		System.out.println("\n\t}");

		System.out.print("\tconstructors {\n\t\t");
		int numConstructors = 0;
		for (ConstructorDef def : m_Constructors) {
			def.debugPrint();
			if (++numConstructors != m_Constructors.size())
				System.out.print("\n\t\t");
		}
		System.out.println("\n\t}");

		System.out.print("\tmethods {\n\t\t");
		int numMethods = 0;
		for (String key : m_Methods.keySet()) {
			m_Methods.get(key).debugPrint();
			if (++numMethods != m_Methods.size())
				System.out.print("\n\t\t");
		}
		System.out.println("\n\t}");

		System.out.println("\n}");
	}

}