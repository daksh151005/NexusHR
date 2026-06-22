import { withSupabase } from "@supabase/server"

export default {
    fetch: withSupabase({ auth: "user" }, async (_req, ctx) => {
        // RLS-scoped client (respects row-level security)
        const { data, error } = await ctx.supabase
            .from("todos")
            .select("*")
            .order("id", { ascending: true })

        if (error) return Response.json({ error: error.message }, { status: 400 })

        // Admin client (bypasses RLS). Useful for operations that must not be restricted.
        // const { data: adminData } = await ctx.supabaseAdmin.from("todos").select("*")

        return Response.json({ todos: data ?? [] })
    }),
}
